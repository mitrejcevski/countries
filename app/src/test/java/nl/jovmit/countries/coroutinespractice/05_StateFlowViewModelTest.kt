package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StateFlowViewModelTest {

    private suspend fun <T> HttpCall<T>.await(): T {
        // You can copy your solution from 01_CallbackToCoroutineTest.
        TODO("Implement await()")
    }

    class UserRepository(
        private val api: UserApi
    ) {
        suspend fun loadProfile(userId: String): UserProfile {
            TODO("Load user and posts")
        }
    }

    /**
     * This is a ViewModel-like class without Android dependencies.
     *
     * In a real ViewModel, you would use viewModelScope instead of passing CoroutineScope.
     */
    class ProfilePresenter(
        private val repository: UserRepository,
        private val scope: CoroutineScope
    ) {
        private val _state = MutableStateFlow(ProfileUiState())
        val state: StateFlow<ProfileUiState> = _state.asStateFlow()

        /**
         * Exercise 9:
         * Implement loading state.
         *
         * Rules:
         * - immediately set isLoading = true
         * - on success, put user and posts in state
         * - on failure, set errorMessage
         * - loading should be false at the end
         */
        fun loadProfile(userId: String) {
            TODO("Launch coroutine and update MutableStateFlow")
        }
    }

    @Test
    fun `load profile updates state on success`() = runTest {
        val api = FakeUserApi(scope = this)
        val presenter = ProfilePresenter(
            repository = UserRepository(api),
            scope = this
        )

        presenter.loadProfile("123")
        advanceUntilIdle()

        val state = presenter.state.value

        assertFalse(state.isLoading)
        assertEquals("123", state.user?.id)
        assertEquals(2, state.posts.size)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `load profile updates state on failure`() = runTest {
        val api = FakeUserApi(scope = this, failUser = true)
        val presenter = ProfilePresenter(
            repository = UserRepository(api),
            scope = this
        )

        presenter.loadProfile("123")
        advanceUntilIdle()

        val state = presenter.state.value

        assertFalse(state.isLoading)
        assertEquals(null, state.user)
        assertTrue(state.errorMessage != null)
    }
}
