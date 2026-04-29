package nl.jovmit.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoadCountriesTest {

  interface CountriesRepository {
    suspend fun getAvailableCountries(): CountiesResult
  }

  private val dispatcher = Dispatchers.Unconfined
  private val mexico = Country(
    name = "Mexico",
    nativeName = "Mexico",
    capital = "Mexico City"
  )

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun noCountriesFound() {
    val viewModel = CountriesViewModel(
      repository = InMemoryCountriesRepository(emptyList()),
      dispatcher = dispatcher
    )

    viewModel.loadCountries()

    assertThat(viewModel.uiState.value)
      .isEqualTo(UiState(countries = emptyList()))
  }

  @Test
  fun aCountryWasFound() {
    val viewModel = CountriesViewModel(
      repository = InMemoryCountriesRepository(listOf(mexico)),
      dispatcher = dispatcher
    )

    viewModel.loadCountries()

    assertThat(viewModel.uiState.value)
      .isEqualTo(UiState(countries = listOf(mexico)))
  }

  @Test
  fun loadingCountriesNotAvailable() {
    val unavailableRepository = InMemoryCountriesRepository().apply { setUnavailable() }
    val viewModel = CountriesViewModel(unavailableRepository, dispatcher)

    viewModel.loadCountries()

    assertThat(viewModel.uiState.value)
      .isEqualTo(UiState(isBackendError = true))
  }

  class CountriesViewModel(
    private val repository: CountriesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
  ) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun loadCountries() {
      viewModelScope.launch {
        val result = withContext(dispatcher) {
          repository.getAvailableCountries()
        }
        when (result) {
          is CountiesResult.Success -> _uiState.update { it.copy(countries = result.countries) }
          is CountiesResult.BackendError -> _uiState.update { it.copy(isBackendError = true) }
        }
      }
    }
  }

  class InMemoryCountriesRepository(
    val countries: List<Country> = emptyList()
  ) : CountriesRepository {

    private var isUnavailable = false

    override suspend fun getAvailableCountries(): CountiesResult {
      return if (isUnavailable) CountiesResult.BackendError else
        CountiesResult.Success(countries)
    }

    fun setUnavailable() {
      isUnavailable = true
    }
  }

  sealed class CountiesResult {
    data class Success(val countries: List<Country>) : CountiesResult()
    data object BackendError : CountiesResult()
  }

  data class UiState(
    val countries: List<Country> = emptyList(),
    val isBackendError: Boolean = false
  )

  data class Country(
    val name: String,
    val nativeName: String,
    val capital: String
  )
}