package nl.jovmit.countries.data.remote

import nl.jovmit.countries.data.model.CountriesListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CountriesApi {

  @GET("/api/countries")
  suspend fun getCountries(
    @Query("page") page: Int = 1
  ): CountriesListResponse

  @GET("/api/countries")
  suspend fun getCountryDetails(
    @Query("name") name: String
  ): CountriesListResponse
}