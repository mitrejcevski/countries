package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryRefreshPatternTest {

    private suspend fun <T> HttpCall<T>.await(): T {
        // You can copy your solution from 01_CallbackToCoroutineTest.
        TODO("Implement await()")
    }

    class UserRepository(
        private val dao: UserDao,
        private val api: UserApi
    ) {
        /**
         * Exercise 13:
         * Observe local database.
         */
        fun observeUser(userId: String): Flow<User> {
            TODO("Return dao.observeUser(userId)")
        }

        /**
         * Exercise 14:
         * Refresh from network and save into local database.
         */
        suspend fun refreshUser(userId: String) {
            TODO("Fetch user from API and save into DAO")
        }
    }

    @Test
    fun `refresh user saves network result into dao`() = runTest {
        val dao = FakeUserDao()
        val api = FakeUserApi(scope = this)
        val repository = UserRepository(dao, api)

        repository.refreshUser("123")

        val savedUser = dao.latestUser()

        assertEquals("123", savedUser?.id)
        assertEquals("John Doe", savedUser?.name)
    }

    @Test
    fun `observe user reads from local database`() = runTest {
        val dao = FakeUserDao()
        val api = FakeUserApi(scope = this)
        val repository = UserRepository(dao, api)

        repository.refreshUser("123")

        val user = repository.observeUser("123").first()

        assertEquals("John Doe", user.name)
    }
}
