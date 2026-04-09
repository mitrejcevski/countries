package nl.jovmit.countries

sealed class CountriesResult {
    data class Success(val countries: List<Country>): CountriesResult()
    data object BackendError: CountriesResult()
    data object ConnectivityError: CountriesResult()
}