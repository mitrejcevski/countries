package nl.jovmit.countries

import org.junit.Test

class PracticeCoroutines {

  @Test
  fun convertToCoroutines() {
    val api = FakeUserApi()

    api.getUser("123").enqueue(object : Callback<User> {
      override fun onSuccess(value: User) {
        println(value)
      }

      override fun onError(error: Throwable) {
        println("Error: ${error.message}")
      }
    })
  }
}

interface Callback<T> {
  fun onSuccess(value: T)
  fun onError(error: Throwable)
}

interface HttpCall<T> {
  fun enqueue(callback: Callback<T>)
  fun cancel()
}

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

interface UserApi {
  fun getUser(userId: String): HttpCall<User>

  fun getPosts(userId: String): HttpCall<List<Post>>

  fun updateUser(user: User): HttpCall<User>
}

class FakeUserApi : UserApi {

  override fun getUser(userId: String): HttpCall<User> {
    return FakeHttpCall {
      User(
        id = userId,
        name = "John Doe",
        email = "john@example.com"
      )
    }
  }

  override fun getPosts(userId: String): HttpCall<List<Post>> {
    return FakeHttpCall {
      listOf(
        Post(
          id = "1",
          userId = userId,
          title = "First post"
        ),
        Post(
          id = "2",
          userId = userId,
          title = "Second post"
        )
      )
    }
  }

  override fun updateUser(user: User): HttpCall<User> {
    return FakeHttpCall {
      user.copy(name = user.name.trim())
    }
  }
}

class FakeHttpCall<T>(
  private val delayMillis: Long = 1_000,
  private val shouldFail: Boolean = false,
  private val responseProvider: () -> T
) : HttpCall<T> {

  private var cancelled = false

  override fun enqueue(callback: Callback<T>) {
    Thread {
      try {
        Thread.sleep(delayMillis)

        if (cancelled) return@Thread

        if (shouldFail) {
          callback.onError(RuntimeException("Network error"))
        } else {
          callback.onSuccess(responseProvider())
        }
      } catch (error: Throwable) {
        if (!cancelled) {
          callback.onError(error)
        }
      }
    }.start()
  }

  override fun cancel() {
    cancelled = true
  }
}