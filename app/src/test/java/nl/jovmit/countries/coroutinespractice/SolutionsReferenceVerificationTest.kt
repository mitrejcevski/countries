package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolutionsReferenceVerificationTest {

    @Test
    fun `exercise 1 await solution handles success failure and cancellation`() = runTest {
        with(SolutionsReference) {
            val successApi = FakeUserApi(scope = this@runTest)
            val successDeferred = async {
                successApi.getUser("123").awaitSolution()
            }

            advanceTimeBy(1_000)

            assertEquals("123", successDeferred.await().id)

            val failureApi = FakeUserApi(scope = this@runTest, failUser = true)
            val failureDeferred = async {
                runCatching {
                    failureApi.getUser("123").awaitSolution()
                }
            }

            advanceTimeBy(1_000)

            val error = failureDeferred.await().exceptionOrNull()

            assertTrue(error is RuntimeException)
            assertEquals("Network error", error?.message)

            val cancellationApi = FakeUserApi(scope = this@runTest)
            val cancellationJob = async {
                cancellationApi.getUser("123").awaitSolution()
            }

            runCurrent()
            cancellationJob.cancelAndJoin()

            assertTrue(cancellationApi.lastUserCall?.wasCancelled == true)
        }
    }

    @Test
    fun `exercises 2 through 4 repository loading solutions work`() = runTest {
        val sequentialRepository = SolutionsReference.UserRepository(
            FakeUserApi(
                scope = this,
                userDelayMillis = 1_000,
                postsDelayMillis = 1_000
            )
        )

        val sequentialProfile = sequentialRepository.loadProfileSequentially("123")

        assertEquals("123", sequentialProfile.user.id)
        assertEquals(2, sequentialProfile.posts.size)
        assertEquals(2_000, currentTime)

        val parallelRepository = SolutionsReference.UserRepository(
            FakeUserApi(
                scope = this,
                userDelayMillis = 1_000,
                postsDelayMillis = 1_000
            )
        )

        val parallelProfile = parallelRepository.loadProfileInParallel("123")

        assertEquals("123", parallelProfile.user.id)
        assertEquals(2, parallelProfile.posts.size)
        assertEquals(3_000, currentTime)

        val safeRepository = SolutionsReference.UserRepository(
            FakeUserApi(scope = this, failPosts = true)
        )

        val safeProfile = safeRepository.loadProfileSafely("123")

        assertEquals("123", safeProfile.user.id)
        assertTrue(safeProfile.posts.isEmpty())

        val failingUserRepository = SolutionsReference.UserRepository(
            FakeUserApi(scope = this, failUser = true)
        )

        val error = runCatching {
            failingUserRepository.loadProfileSafely("123")
        }.exceptionOrNull()

        assertTrue(error is RuntimeException)
        assertEquals("Network error", error?.message)
    }

    @Test
    fun `exercises 5 and 6 flow basics solutions work`() = runTest {
        val repository = SolutionsReference.UserRepository(
            api = FakeUserApi(scope = this),
            dao = FakeUserDao()
        )

        val firstUser = repository.observeUser("123").first()
        val uiModel = repository.observeUserUiModel("123").first()
        val users = repository.observeUser("123").take(3).toList()

        assertEquals("123", firstUser.id)
        assertEquals("Loading name...", firstUser.name)
        assertEquals("Loading name...", uiModel.title)
        assertEquals("loading@example.com", uiModel.subtitle)
        assertEquals(
            listOf("Loading name...", "John Doe", "John Updated"),
            users.map { it.name }
        )
    }

    @Test
    fun `exercises 7 and 8 search solutions work`() = runTest {
        val ignoredApi = FakeSearchApi()
        val ignoredRepository = SolutionsReference.SearchRepository(ignoredApi)

        val ignoredResults = ignoredRepository.searchResults(flowOf("a", "ab")).toList()

        assertEquals(emptyList<List<SearchResult>>(), ignoredResults)
        assertEquals(emptyList<String>(), ignoredApi.queries)

        val successApi = FakeSearchApi(delayMillis = 1_000)
        val successRepository = SolutionsReference.SearchRepository(successApi)

        val results = successRepository.searchResults(flowOf("android")).toList()

        assertEquals(1, results.size)
        assertEquals(2, results.first().size)
        assertEquals(listOf("android"), successApi.queries)

        val stateApi = FakeSearchApi(delayMillis = 1_000)
        val stateRepository = SolutionsReference.SearchRepository(stateApi)

        val successStates = stateRepository.searchUiState(flowOf("android")).toList()

        assertEquals(SearchUiState.Loading, successStates[0])
        assertTrue(successStates[1] is SearchUiState.Success)

        val idleStates = stateRepository.searchUiState(flowOf("ab")).toList()

        assertEquals(listOf(SearchUiState.Idle), idleStates)

        val errorRepository = SolutionsReference.SearchRepository(FakeSearchApi(shouldFail = true))
        val errorStates = errorRepository.searchUiState(flowOf("android")).toList()

        assertEquals(SearchUiState.Loading, errorStates[0])
        assertTrue(errorStates[1] is SearchUiState.Error)

        val cancellationApi = FakeSearchApi(delayMillis = 1_000)
        val cancellationRepository = SolutionsReference.SearchRepository(cancellationApi)
        val queryFlow = MutableSharedFlow<String>()
        val cancellationResults = mutableListOf<List<SearchResult>>()
        val collectionJob = launch {
            cancellationRepository.searchResults(queryFlow).toList(cancellationResults)
        }

        runCurrent()
        queryFlow.emit("android")
        advanceTimeBy(300)
        runCurrent()
        advanceTimeBy(500)
        queryFlow.emit("kotlin")
        advanceTimeBy(300)
        runCurrent()
        advanceTimeBy(1_000)
        runCurrent()

        assertEquals(listOf("android", "kotlin"), cancellationApi.queries)
        assertEquals(1, cancellationResults.size)
        assertEquals("kotlin result 1", cancellationResults.first().first().title)

        collectionJob.cancel()
    }

    @Test
    fun `exercise 9 state flow presenter solution works`() = runTest {
        val successPresenter = SolutionsReference.ProfileStatePresenter(
            repository = SolutionsReference.UserRepository(FakeUserApi(scope = this)),
            scope = this
        )

        successPresenter.loadProfile("123")
        advanceUntilIdle()

        val successState = successPresenter.state.value

        assertFalse(successState.isLoading)
        assertEquals("123", successState.user?.id)
        assertEquals(2, successState.posts.size)
        assertNull(successState.errorMessage)

        val failurePresenter = SolutionsReference.ProfileStatePresenter(
            repository = SolutionsReference.UserRepository(FakeUserApi(scope = this, failUser = true)),
            scope = this
        )

        failurePresenter.loadProfile("123")
        advanceUntilIdle()

        val failureState = failurePresenter.state.value

        assertFalse(failureState.isLoading)
        assertNull(failureState.user)
        assertTrue(failureState.errorMessage != null)
    }

    @Test
    fun `exercises 10 and 11 shared flow event presenter solution works`() = runTest {
        val failurePresenter = SolutionsReference.ProfileEventPresenter(
            repository = SolutionsReference.UserRepository(FakeUserApi(scope = this, failUser = true)),
            scope = this
        )
        val snackbarDeferred = async {
            failurePresenter.events.first()
        }

        runCurrent()
        failurePresenter.loadProfile("123")
        advanceUntilIdle()

        assertEquals(
            ProfileEvent.ShowSnackbar("Could not load profile"),
            snackbarDeferred.await()
        )

        val backPresenter = SolutionsReference.ProfileEventPresenter(
            repository = SolutionsReference.UserRepository(FakeUserApi(scope = this)),
            scope = this
        )
        val backDeferred = async {
            backPresenter.events.first()
        }

        runCurrent()
        backPresenter.onBackClicked()
        advanceUntilIdle()

        assertEquals(ProfileEvent.NavigateBack, backDeferred.await())
    }

    @Test
    fun `exercises 12 through 14 combine and refresh solutions work`() = runTest {
        val homeRepository = SolutionsReference.HomeRepository()

        val visibleEmailState = homeRepository.homeUiState(
            userFlow = flowOf(User("123", "John Doe", "john@example.com")),
            settingsFlow = flowOf(UserSettings(showEmail = true))
        ).first()

        assertEquals("John Doe", visibleEmailState.name)
        assertEquals("john@example.com", visibleEmailState.email)

        val hiddenEmailState = homeRepository.homeUiState(
            userFlow = flowOf(User("123", "John Doe", "john@example.com")),
            settingsFlow = flowOf(UserSettings(showEmail = false))
        ).first()

        assertEquals("John Doe", hiddenEmailState.name)
        assertNull(hiddenEmailState.email)

        val dao = FakeUserDao()
        val userRepository = SolutionsReference.UserRepository(
            api = FakeUserApi(scope = this),
            dao = dao
        )

        userRepository.refreshUser("123")

        val savedUser = dao.latestUser()
        val observedUser = userRepository.observeUser("123").first()

        assertEquals("123", savedUser?.id)
        assertEquals("John Doe", savedUser?.name)
        assertEquals("John Doe", observedUser.name)
    }
}
