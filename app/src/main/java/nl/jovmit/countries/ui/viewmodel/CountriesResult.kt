package nl.jovmit.countries.ui.viewmodel

import nl.jovmit.countries.data.model.Country

sealed class CountriesResult {
    data class Success(val countries: List<Country>): CountriesResult()
    data object BackendError: CountriesResult()
    data object ConnectivityError: CountriesResult()
}