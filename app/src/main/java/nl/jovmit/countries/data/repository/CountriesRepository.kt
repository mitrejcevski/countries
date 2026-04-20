package nl.jovmit.countries.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nl.jovmit.countries.data.local.CountryDao
import nl.jovmit.countries.data.local.toDomain
import nl.jovmit.countries.data.local.toEntity
import nl.jovmit.countries.data.model.CountryDetailsResponse
import nl.jovmit.countries.data.remote.CountriesApi
import nl.jovmit.countries.ui.viewmodel.CountriesResult
import nl.jovmit.countries.ui.viewmodel.CountryDetailsResult
import okio.IOException
import retrofit2.HttpException

// Mapping
// Transform exception to domain Type
// Separating the responsibility

class CountriesRepository(
  private val api: CountriesApi,
  private val dao: CountryDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  suspend fun getCountries(page: Int = 1): CountriesResult {
    return try {
      val countries = loadCountries().map { it.toDomain() }
      CountriesResult.Success(countries)
    } catch (e: HttpException) {
      CountriesResult.BackendError
    } catch (e: IOException) {
      CountriesResult.ConnectivityError
    }
  }

  fun observeCountries(): Flow<CountriesResult> {
    return dao.getAll().map { countryEntities ->
      val countries = countryEntities.map { it.toDomain() }
      CountriesResult.Success(countries)
    }.flowOn(dispatcher)
  }

  suspend fun getCountryDetails(name: String): CountryDetailsResult {
    return try {
      val country = loadCountries().map { it.toDomain() }
      CountryDetailsResult.Success(country.first())
    } catch (e: HttpException) {
      CountryDetailsResult.BackendError
    } catch (e: IOException) {
      CountryDetailsResult.ConnectivityError
    }
  }

  private suspend fun loadCountries(): List<CountryDetailsResponse> {
    val countries = api.getCountries(page = 1).countries
    dao.insertAll(*countries.map { it.toEntity() }.toTypedArray())
    return countries
  }
}