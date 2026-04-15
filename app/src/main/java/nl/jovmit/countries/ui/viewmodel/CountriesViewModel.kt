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
import nl.jovmit.countries.ui.viewmodel.CountriesResult
import nl.jovmit.countries.data.model.Country

sealed class CountriesListUiState {
    data object Idle : CountriesListUiState()
    data class Countries(
        val countries: List<Country>,
    ) : CountriesListUiState()

    sealed class Error : CountriesListUiState() {
        data object BackendError : Error()
        data object ConnectivityError : Error()
    }
}

class CountriesViewModel(
    private val repository: CountriesRepository,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountriesListUiState>(CountriesListUiState.Idle)
    val uiState: StateFlow<CountriesListUiState> = _uiState

    fun loadCountries() {
        viewModelScope.launch {
            val result = withContext(dispatcher) {
                repository.getCountries()
            }

            when (result) {
                is CountriesResult.Success -> {
                    _uiState.update { CountriesListUiState.Countries(result.countries) }
                }

                is CountriesResult.BackendError -> {
                    _uiState.update {
                        CountriesListUiState.Error.BackendError
                    }
                }

                is CountriesResult.ConnectivityError -> {
                    _uiState.update {
                        CountriesListUiState.Error.ConnectivityError
                    }

                }
            }
        }
    }

    fun toggleFavorite(countryName: String) {
        _uiState.update { currentState ->

            when (currentState) {
                is CountriesListUiState.Countries -> {
                    val updatedList = currentState.countries
                        .map {
                            if (it.name == countryName) {
                                it.copy(isFavorite = !it.isFavorite)
                            } else it
                        }
                    currentState.copy(countries = updatedList)
                }
                else -> currentState
            }
        }
    }

}