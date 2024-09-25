package com.example.cloud.repository.remote

import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import kotlinx.coroutines.flow.Flow

interface WeatherRepositoryInterface {
    fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Daily>
    fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Hourly>
    fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Daily>
    fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Triple<Daily, Hourly, Daily>>
}


