package com.example.cloud.database

import androidx.room.*

@Dao
interface WeatherDao {
    /*@Query("SELECT COUNT(*) FROM current_weather WHERE temperature = :temperature")
    suspend fun isWeatherDataExists(temperature: String): Double*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather")
    suspend fun getAllWeatherData(): List<CurrentWeatherEntity>

    @Query("SELECT * FROM current_weather LIMIT 1")
    suspend fun getFirstWeatherItem(): CurrentWeatherEntity?

    @Delete
    suspend fun deleteWeather(weather: CurrentWeatherEntity)




}






