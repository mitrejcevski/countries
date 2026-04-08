package nl.jovmit.countries

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CountriesListUiState {
  data object Idle : CountriesListUiState()
  data class Countries(val countries: List<Country>) : CountriesListUiState()
  sealed class Error : CountriesListUiState() {
    data object BackendError : Error()
    data object ConnectivityError : Error()
  }
}

class CountriesViewModel : ViewModel() {

  private val _uiState = MutableStateFlow<CountriesListUiState>(CountriesListUiState.Idle)
  val uiState: StateFlow<CountriesListUiState> = _uiState

  fun loadCountries() {
    //TODO load countries and update the UI state
  }
}