package com.example.cloud.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather")
    fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>>

    @Query("SELECT * FROM current_weather LIMIT 1")
    suspend fun getFirstWeatherItem(): CurrentWeatherEntity?

    @Delete
    suspend fun deleteWeather(weather: CurrentWeatherEntity)




}





