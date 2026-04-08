package nl.jovmit.countries

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountriesListResponse(
  @SerialName("page") val page: Int,
  @SerialName("per_page") val pageSize: Int,
  @SerialName("total") val totalItems: Int,
  @SerialName("total_pages") val totalPages: Int,
  @SerialName("data") val countries: List<CountryDetailsResponse>
)