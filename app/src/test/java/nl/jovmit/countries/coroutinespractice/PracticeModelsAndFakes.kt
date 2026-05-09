package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

// ------------------------------------------------------------
// Models
// ------------------------------------------------------------

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Post(
    val id: String,
    val userId: String,
    val title: String
)

data class Comment(
    val id: String,
    val postId: String,
    val body: String
)

data class UserProfile(
    val user: User,
    val posts: List<Post>
)

data class SearchResult(
    val id: String,
    val title: String
)

data class UserUiModel(
    val title: String,
    val subtitle: String
)

data class UserSettings(
    val showEmail: Boolean
)

data class HomeUiState(
    val name: String,
    val email: String?
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val errorMessage: String? = null
)

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<SearchResult>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

sealed interface ProfileEvent {
    data class ShowSnackbar(val message: String) : ProfileEvent
    data object NavigateBack : ProfileEvent
}

// ------------------------------------------------------------
// Callback-based HTTP API
// ------------------------------------------------------------

interface Callback<T> {
    fun onSuccess(value: T)
    fun onError(error: Throwable)
}

interface HttpCall<T> {
    fun enqueue(callback: Callback<T>)
    fun cancel()
}

/**
 * Fake callback-based HTTP call.
 *
 * Important:
 * This uses a CoroutineScope so tests can control time with runTest.
 */
class FakeHttpCall<T>(
    private val scope: CoroutineScope,
    private val delayMillis: Long = 1_000,
    private val shouldFail: Boolean = false,
    private val error: Throwable = RuntimeException("Network error"),
    private val responseProvider: () -> T
) : HttpCall<T> {

    var wasCancelled: Boolean = false
        private set

    private var job: Job? = null

    override fun enqueue(callback: Callback<T>) {
        job = scope.launch {
            delay(delayMillis)

            if (wasCancelled) return@launch

            if (shouldFail) {
                callback.onError(error)
            } else {
                callback.onSuccess(responseProvider())
            }
        }
    }

    override fun cancel() {
        wasCancelled = true
        job?.cancel()
    }
}

// ------------------------------------------------------------
// API interfaces
// ------------------------------------------------------------

interface UserApi {
    fun getUser(userId: String): HttpCall<User>
    fun getPosts(userId: String): HttpCall<List<Post>>
    fun getComments(postId: String): HttpCall<List<Comment>>
    fun updateUser(user: User): HttpCall<User>
}

class FakeUserApi(
    private val scope: CoroutineScope,
    var failUser: Boolean = false,
    var failPosts: Boolean = false,
    var userDelayMillis: Long = 1_000,
    var postsDelayMillis: Long = 1_000
) : UserApi {

    var lastUserCall: FakeHttpCall<User>? = null
        private set

    override fun getUser(userId: String): HttpCall<User> {
        return FakeHttpCall(
            scope = scope,
            delayMillis = userDelayMillis,
            shouldFail = failUser
        ) {
            User(
                id = userId,
                name = "John Doe",
                email = "john@example.com"
            )
        }.also {
            lastUserCall = it
        }
    }

    override fun getPosts(userId: String): HttpCall<List<Post>> {
        return FakeHttpCall(
            scope = scope,
            delayMillis = postsDelayMillis,
            shouldFail = failPosts
        ) {
            listOf(
                Post(id = "post-1", userId = userId, title = "First post"),
                Post(id = "post-2", userId = userId, title = "Second post")
            )
        }
    }

    override fun getComments(postId: String): HttpCall<List<Comment>> {
        return FakeHttpCall(scope = scope) {
            listOf(
                Comment(id = "comment-1", postId = postId, body = "Nice post")
            )
        }
    }

    override fun updateUser(user: User): HttpCall<User> {
        return FakeHttpCall(scope = scope) {
            user.copy(name = user.name.trim())
        }
    }
}

// ------------------------------------------------------------
// Fake database
// ------------------------------------------------------------

interface UserDao {
    fun observeUser(userId: String): Flow<User>
    suspend fun saveUser(user: User)
    suspend fun latestUser(): User?
}

class FakeUserDao : UserDao {

    private var storedUser: User? = null

    override fun observeUser(userId: String): Flow<User> = flow {
        storedUser?.let { emit(it) }

        emit(User(userId, "Loading name...", "loading@example.com"))
        delay(1_000)

        storedUser?.let {
            emit(it)
        } ?: emit(User(userId, "John Doe", "john@example.com"))

        delay(1_000)

        storedUser?.let {
            emit(it)
        } ?: emit(User(userId, "John Updated", "john.updated@example.com"))
    }

    override suspend fun saveUser(user: User) {
        storedUser = user
    }

    override suspend fun latestUser(): User? {
        return storedUser
    }
}

// ------------------------------------------------------------
// Search API
// ------------------------------------------------------------

interface SearchApi {
    suspend fun search(query: String): List<SearchResult>
}

class FakeSearchApi(
    private val delayMillis: Long = 1_000,
    private val shouldFail: Boolean = false
) : SearchApi {

    val queries = mutableListOf<String>()

    override suspend fun search(query: String): List<SearchResult> {
        queries += query
        delay(delayMillis)

        if (shouldFail) {
            throw RuntimeException("Search failed")
        }

        return listOf(
            SearchResult(id = "1", title = "$query result 1"),
            SearchResult(id = "2", title = "$query result 2")
        )
    }
}
