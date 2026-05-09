package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CombineFlowsTest {

    class HomeRepository {
        /**
         * Exercise 12:
         * Combine user and settings into HomeUiState.
         *
         * Rules:
         * - always show name
         * - show email only if settings.showEmail is true
         */
        fun homeUiState(
            userFlow: Flow<User>,
            settingsFlow: Flow<UserSettings>
        ): Flow<HomeUiState> {
            TODO("Use combine")
        }
    }

    @Test
    fun `home state shows email when setting allows it`() = runTest {
        val repository = HomeRepository()

        val state = repository.homeUiState(
            userFlow = flowOf(User("123", "John Doe", "john@example.com")),
            settingsFlow = flowOf(UserSettings(showEmail = true))
        ).first()

        assertEquals("John Doe", state.name)
        assertEquals("john@example.com", state.email)
    }

    @Test
    fun `home state hides email when setting disables it`() = runTest {
        val repository = HomeRepository()

        val state = repository.homeUiState(
            userFlow = flowOf(User("123", "John Doe", "john@example.com")),
            settingsFlow = flowOf(UserSettings(showEmail = false))
        ).first()

        assertEquals("John Doe", state.name)
        assertEquals(null, state.email)
    }
}
