package nl.jovmit.countries.data.model

data class Country(
  val name: String,
  val population: Int,
  val capital: String,
  val language: String,
  val nativeName: String,
  val isFavorite: Boolean = false
)