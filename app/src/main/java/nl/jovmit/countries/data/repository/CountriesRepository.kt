package nl.jovmit.countries.data.repository

import nl.jovmit.countries.data.model.Country
import nl.jovmit.countries.data.remote.CountriesApi
import nl.jovmit.countries.ui.viewmodel.CountriesResult
import nl.jovmit.countries.ui.viewmodel.CountryDetailsResult
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

    suspend fun getCountryDetails(name: String): CountryDetailsResult {
        return try {
            val country = api.getCountryDetails(name).countries.first().let { c ->
                Country(
                    c.name,
                    c.population,
                    c.capital,
                    c.language.first(),
                    c.nativeName
                )
            }
            CountryDetailsResult.Success(country)
        } catch (e: HttpException) {
            CountryDetailsResult.BackendError
        } catch (e: IOException) {
            CountryDetailsResult.ConnectivityError
        }
    }
}