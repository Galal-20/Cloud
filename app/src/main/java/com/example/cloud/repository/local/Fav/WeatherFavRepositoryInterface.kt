package com.example.cloud.repository.local.Fav

import com.example.cloud.database.entity.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherFavRepositoryInterface {
    suspend fun insertWeather(weather: CurrentWeatherEntity)
    fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>>
    suspend fun getFirstWeatherItem(): CurrentWeatherEntity?
    suspend fun deleteWeather(weather: CurrentWeatherEntity)
}


