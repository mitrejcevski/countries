package nl.jovmit.countries.coroutinespractice

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SequentialAndParallelCoroutinesTest {

    private suspend fun <T> HttpCall<T>.await(): T {
        // You can copy your solution from 01_CallbackToCoroutineTest.
        TODO("Implement await()")
    }

    class UserRepository(
        private val api: UserApi
    ) {
        suspend fun getUser(userId: String): User {
            TODO("Call api.getUser(userId).await()")
        }

        suspend fun getPosts(userId: String): List<Post> {
            TODO("Call api.getPosts(userId).await()")
        }

        /**
         * Exercise 2:
         * Load user and posts sequentially.
         */
        suspend fun loadProfileSequentially(userId: String): UserProfile {
            TODO("Load user, then posts")
        }

        /**
         * Exercise 3:
         * Load user and posts in parallel using async.
         */
        suspend fun loadProfileInParallel(userId: String): UserProfile = coroutineScope {
            TODO("Load user and posts in parallel")
        }

        /**
         * Exercise 4:
         * User is required.
         * Posts are optional.
         *
         * If posts fail, return an empty post list.
         * If user fails, throw.
         */
        suspend fun loadProfileSafely(userId: String): UserProfile = supervisorScope {
            TODO("Use supervisorScope and runCatching for posts")
        }
    }

    @Test
    fun `sequential loading takes sum of both calls`() = runTest {
        val api = FakeUserApi(
            scope = this,
            userDelayMillis = 1_000,
            postsDelayMillis = 1_000
        )
        val repository = UserRepository(api)

        val profile = repository.loadProfileSequentially("123")

        assertEquals("123", profile.user.id)
        assertEquals(2, profile.posts.size)
        assertEquals(2_000, currentTime)
    }

    @Test
    fun `parallel loading takes only the slowest call time`() = runTest {
        val api = FakeUserApi(
            scope = this,
            userDelayMillis = 1_000,
            postsDelayMillis = 1_000
        )
        val repository = UserRepository(api)

        val profile = repository.loadProfileInParallel("123")

        assertEquals("123", profile.user.id)
        assertEquals(2, profile.posts.size)
        assertEquals(1_000, currentTime)
    }

    @Test
    fun `safe loading returns empty posts if posts fail`() = runTest {
        val api = FakeUserApi(
            scope = this,
            failPosts = true
        )
        val repository = UserRepository(api)

        val profile = repository.loadProfileSafely("123")

        assertEquals("123", profile.user.id)
        assertTrue(profile.posts.isEmpty())
    }
}
