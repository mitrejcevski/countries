package nl.jovmit.countries

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CountryDetailUiState {
  data object Idle : CountryDetailUiState()
  data class Countries(val country: Country) : CountryDetailUiState()
  sealed class Error : CountryDetailUiState() {
    data object BackendError : Error()
    data object ConnectivityError : Error()
  }
}

class CountryDetailViewModel : ViewModel() {

  private val _uiState = MutableStateFlow<CountryDetailUiState>(CountryDetailUiState.Idle)
  val uiState: StateFlow<CountryDetailUiState> = _uiState

  fun loadCountryDetails(countryName: String) {
    //TODO fetch country by name and update the UI state
  }
}