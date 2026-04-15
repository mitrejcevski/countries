package nl.jovmit.countries.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.jovmit.countries.data.repository.CountriesRepository
import nl.jovmit.countries.data.model.Country

sealed class CountryDetailUiState {
    data object Idle : CountryDetailUiState()
    data class CountryInfo(
        val country: Country
    ) : CountryDetailUiState()

    sealed class Error : CountryDetailUiState() {
        data object BackendError : Error()
        data object ConnectivityError : Error()
    }
}

class CountryDetailViewModel(
    val repository: CountriesRepository, val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryDetailUiState>(CountryDetailUiState.Idle)
    val uiState: StateFlow<CountryDetailUiState> = _uiState

    fun loadCountryDetails(name: String) {
        viewModelScope.launch {
            val result = withContext(dispatcher) {
                repository.getCountryDetails(name)
            }

            when (val state = result) {
                is CountryDetailsResult.Success -> {
                    _uiState.update { CountryDetailUiState.CountryInfo(state.country) }
                }

                is CountryDetailsResult.BackendError -> {
                    _uiState.update {
                        CountryDetailUiState.Error.BackendError
                    }
                }

                is CountryDetailsResult.ConnectivityError -> {
                    _uiState.update {
                        CountryDetailUiState.Error.ConnectivityError
                    }
                }
            }
        }
    }
}