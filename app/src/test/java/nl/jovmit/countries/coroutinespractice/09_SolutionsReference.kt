package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Optional reference file.
 *
 * Keep this file outside your active test package if you want to avoid spoilers.
 * Or use it only after trying the exercises.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
object SolutionsReference {

    suspend fun <T> HttpCall<T>.awaitSolution(): T {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onSuccess(value: T) {
                    if (continuation.isActive) {
                        continuation.resume(value)
                    }
                }

                override fun onError(error: Throwable) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
            })

            continuation.invokeOnCancellation {
                cancel()
            }
        }
    }

    class UserRepository(
        private val api: UserApi,
        private val dao: UserDao? = null
    ) {
        suspend fun getUser(userId: String): User {
            return api.getUser(userId).awaitSolution()
        }

        suspend fun getPosts(userId: String): List<Post> {
            return api.getPosts(userId).awaitSolution()
        }

        suspend fun loadProfileSequentially(userId: String): UserProfile {
            val user = getUser(userId)
            val posts = getPosts(userId)
            return UserProfile(user, posts)
        }

        suspend fun loadProfile(userId: String): UserProfile {
            return loadProfileSequentially(userId)
        }

        suspend fun loadProfileInParallel(userId: String): UserProfile = coroutineScope {
            val user = async { getUser(userId) }
            val posts = async { getPosts(userId) }

            UserProfile(
                user = user.await(),
                posts = posts.await()
            )
        }

        suspend fun loadProfileSafely(userId: String): UserProfile = supervisorScope {
            val user = async { getUser(userId) }
            val posts = async {
                runCatching { getPosts(userId) }
                    .getOrDefault(emptyList())
            }

            UserProfile(
                user = user.await(),
                posts = posts.await()
            )
        }

        fun observeUser(userId: String): Flow<User> {
            return requireNotNull(dao).observeUser(userId)
        }

        fun observeUserUiModel(userId: String): Flow<UserUiModel> {
            return observeUser(userId)
                .map { user ->
                    UserUiModel(
                        title = user.name,
                        subtitle = user.email
                    )
                }
        }

        suspend fun refreshUser(userId: String) {
            val user = api.getUser(userId).awaitSolution()
            requireNotNull(dao).saveUser(user)
        }
    }

    class ProfileStatePresenter(
        private val repository: UserRepository,
        private val scope: CoroutineScope
    ) {
        private val _state = MutableStateFlow(ProfileUiState())
        val state: StateFlow<ProfileUiState> = _state.asStateFlow()

        fun loadProfile(userId: String) {
            scope.launch {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                runCatching {
                    repository.loadProfile(userId)
                }.onSuccess { profile ->
                    _state.value = ProfileUiState(
                        isLoading = false,
                        user = profile.user,
                        posts = profile.posts,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    class ProfileEventPresenter(
        private val repository: UserRepository,
        private val scope: CoroutineScope
    ) {
        private val _events = MutableSharedFlow<ProfileEvent>()
        val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

        fun loadProfile(userId: String) {
            scope.launch {
                runCatching {
                    repository.loadProfile(userId)
                }.onFailure {
                    _events.emit(ProfileEvent.ShowSnackbar("Could not load profile"))
                }
            }
        }

        fun onBackClicked() {
            scope.launch {
                _events.emit(ProfileEvent.NavigateBack)
            }
        }
    }

    class SearchRepository(
        private val api: SearchApi
    ) {
        fun searchResults(queryFlow: Flow<String>): Flow<List<SearchResult>> {
            return queryFlow
                .debounce(300)
                .filter { it.length >= 3 }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        emit(api.search(query))
                    }
                }
        }

        fun searchUiState(queryFlow: Flow<String>): Flow<SearchUiState> {
            return queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.length < 3) {
                        flowOf(SearchUiState.Idle)
                    } else {
                        flow {
                            emit(SearchUiState.Loading)
                            val results = api.search(query)
                            emit(SearchUiState.Success(results))
                        }.catch { error ->
                            emit(SearchUiState.Error(error.message ?: "Unknown error"))
                        }
                    }
                }
        }
    }

    class HomeRepository {
        fun homeUiState(
            userFlow: Flow<User>,
            settingsFlow: Flow<UserSettings>
        ): Flow<HomeUiState> {
            return combine(userFlow, settingsFlow) { user, settings ->
                HomeUiState(
                    name = user.name,
                    email = if (settings.showEmail) user.email else null
                )
            }
        }
    }
}
