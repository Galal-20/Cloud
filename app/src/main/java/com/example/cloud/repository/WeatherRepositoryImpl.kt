package com.example.cloud.repository

import android.util.Log
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.retrofit.ApiClient
import com.example.cloud.utils.Secret
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.IOException

class WeatherRepositoryImpl: WeatherRepositoryInterface {

    private val apiService = ApiClient.retrofit

    override suspend fun fetchWeatherByCoordinates(lat: Double, lon: Double): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response =apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Weather fetched successfully by coordinates: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving weather data by coordinates"))
                }
            } catch (e: IOException) {
                Result.failure(Throwable("Network error: ${e.message}"))
            }
        }
    }

    override suspend fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Result<Hourly> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Hourly forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving hourly forecast data"))
                }
            } catch (e: IOException) {
                Result.failure(Throwable("Network error: ${e.message}"))
            }
        }
    }

    override suspend fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDailyForecastByCoordinates(lat, lon, 7, Secret.appId, Secret.units)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Daily forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving daily forecast data"))
                }
            } catch (e: IOException) {
                Result.failure(Throwable("Network error: ${e.message}"))
            }
        }
    }

    override suspend fun getWeatherDataForNotification(lat: Double, lon: Double): Result<Triple<Daily, Hourly,
            Daily>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentWeatherResponse = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units)

                val hourlyForecastResponse = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units)

                val dailyForecastResponse = apiService.getDailyForecastByCoordinates(lat, lon, 7, Secret.appId, Secret.units)

                if (currentWeatherResponse.isSuccessful && hourlyForecastResponse.isSuccessful && dailyForecastResponse.isSuccessful &&
                    currentWeatherResponse.body() != null && hourlyForecastResponse.body() != null && dailyForecastResponse.body() != null) {

                    Log.d("WeatherRepository", "Weather data for notifications fetched successfully")
                    Result.success(Triple(currentWeatherResponse.body()!!, hourlyForecastResponse.body()!!, dailyForecastResponse.body()!!))
                } else {
                    Result.failure(Throwable("Error retrieving weather data for notifications"))
                }
            } catch (e: IOException) {
                Result.failure(Throwable("Network error: ${e.message}"))
            }
        }
    }
}










