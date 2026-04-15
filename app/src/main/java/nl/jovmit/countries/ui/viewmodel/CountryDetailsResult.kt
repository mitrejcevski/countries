package nl.jovmit.countries.ui.viewmodel

import nl.jovmit.countries.data.model.Country

sealed class CountryDetailsResult {
    data class Success(val country: Country): CountryDetailsResult()
    data object BackendError: CountryDetailsResult()
    data object ConnectivityError: CountryDetailsResult()
}