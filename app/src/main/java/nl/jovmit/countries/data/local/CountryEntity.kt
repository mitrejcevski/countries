package nl.jovmit.countries.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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
