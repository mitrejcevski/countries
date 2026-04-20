package nl.jovmit.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

  @Query("SELECT * FROM countries")
  fun getAll(): Flow<List<CountryEntity>>

  @Insert
  suspend fun insertAll(vararg country: CountryEntity)

  @Query("SELECT * FROM countries WHERE name = :name")
  suspend fun getByName(name: String): CountryEntity?
}