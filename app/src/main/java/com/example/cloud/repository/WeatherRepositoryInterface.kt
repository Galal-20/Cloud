package com.example.cloud.repository

import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly

interface WeatherRepositoryInterface {
    suspend fun fetchWeatherByCoordinates(lat: Double, lon: Double): Result<Daily>
    suspend fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Result<Hourly>
    suspend fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Result<Daily>
    suspend fun getWeatherDataForNotification(lat: Double, lon: Double): Result<Triple<Daily, Hourly, Daily>>
}