package nl.jovmit.countries

import okio.IOException
import retrofit2.HttpException

// Mapping
// Transform exception to domain Type
// Separating the responsibility

class CountriesRepository(
    private val api: CountriesApi
) {
    suspend fun getCountries(page: Int = 1): CountriesResult {
        return try {
            val countries = api.getCountries(page).countries.map { c ->
                Country(
                    c.name,
                    c.population,
                    c.capital,
                    c.language.first(),
                    c.nativeName
                )
            }
            CountriesResult.Success(countries)
        } catch (e: HttpException) {
            CountriesResult.BackendError
        } catch (e: IOException) {
            CountriesResult.ConnectivityError
        }
    }

//    suspend fun fetchCountryDetails(name: String): CountryDetailsResult {
//
//    }
}