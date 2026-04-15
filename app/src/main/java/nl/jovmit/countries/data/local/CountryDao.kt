package nl.jovmit.countries.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries")
    suspend fun getAll(): List<CountryEntity>

    @Query("SELECT * FROM countries WHERE name = :name")
    suspend fun getByName(name: String): CountryEntity?
}