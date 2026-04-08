package nl.jovmit.countries

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDetailsResponse(
  @SerialName("alpha2Code") val alpha2Code: String,
  @SerialName("alpha3Code") val alpha3Code: String,
  @SerialName("altSpellings") val altSpellings: List<String>,
  @SerialName("area") val area: Int,
  @SerialName("borders") val borders: List<String>,
  @SerialName("callingCodes") val callingCodes: List<String>,
  @SerialName("capital") val capital: String,
  @SerialName("currencies") val currencies: List<String>,
  @SerialName("language") val language: List<String>,
  @SerialName("languages") val languages: List<String>,
  @SerialName("name") val name: String,
  @SerialName("nativeName") val nativeName: String,
  @SerialName("numericCode") val numericCode: String,
  @SerialName("population") val population: Int,
  @SerialName("region") val region: String,
  @SerialName("relevance") val relevance: String,
  @SerialName("subregion") val subregion: String,
)