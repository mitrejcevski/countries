package nl.jovmit.countries.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.jovmit.countries.data.model.Country
import nl.jovmit.countries.data.model.CountryDetailsResponse

@Entity(tableName = "countries")
data class CountryEntity(
  @PrimaryKey val name: String,
  val capital: String,
  val region: String,
  val population: Long,
  val language: String,
  val nativeName: String,
  val isFavorite: Boolean
)

fun CountryEntity.toDomain(): Country {
  return Country(
    name = name,
    population = population.toInt(),
    capital = capital,
    language = language,
    nativeName = nativeName,
    isFavorite = isFavorite
  )
}

fun CountryDetailsResponse.toDomain(): Country {
  return Country(
    name = name,
    population = population,
    capital = capital,
    language = language.first(),
    nativeName = nativeName,
    isFavorite = false
  )
}

fun CountryDetailsResponse.toEntity(): CountryEntity {
  return CountryEntity(
    name = name,
    capital = capital,
    region = region,
    population = population.toLong(),
    language = languages.first(),
    nativeName = nativeName,
    isFavorite = false
  )
}