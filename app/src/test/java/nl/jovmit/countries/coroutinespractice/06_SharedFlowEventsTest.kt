package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedFlowEventsTest {

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

    class ProfilePresenter(
        private val repository: UserRepository,
        private val scope: CoroutineScope
    ) {
        private val _events = MutableSharedFlow<ProfileEvent>()
        val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

        /**
         * Exercise 10:
         * Emit a snackbar event when loading fails.
         */
        fun loadProfile(userId: String) {
            TODO("Launch coroutine, call repository, emit ShowSnackbar on failure")
        }

        /**
         * Exercise 11:
         * Emit NavigateBack.
         */
        fun onBackClicked() {
            TODO("Emit ProfileEvent.NavigateBack")
        }
    }

    @Test
    fun `load profile emits snackbar event on error`() = runTest {
        val api = FakeUserApi(scope = this, failUser = true)
        val presenter = ProfilePresenter(
            repository = UserRepository(api),
            scope = this
        )

        val eventDeferred = async {
            presenter.events.first()
        }

        presenter.loadProfile("123")
        advanceUntilIdle()

        val event = eventDeferred.await()

        assertEquals(
            ProfileEvent.ShowSnackbar("Could not load profile"),
            event
        )
    }

    @Test
    fun `back click emits navigate back event`() = runTest {
        val api = FakeUserApi(scope = this)
        val presenter = ProfilePresenter(
            repository = UserRepository(api),
            scope = this
        )

        val eventDeferred = async {
            presenter.events.first()
        }

        presenter.onBackClicked()
        advanceUntilIdle()

        assertEquals(ProfileEvent.NavigateBack, eventDeferred.await())
    }
}
