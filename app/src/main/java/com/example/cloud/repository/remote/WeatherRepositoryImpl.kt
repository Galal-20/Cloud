package com.example.cloud.repository.remote

import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.retrofit.ApiClient
import com.example.cloud.utils.Secret
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class WeatherRepositoryImpl : WeatherRepositoryInterface {

    private val apiService = ApiClient.retrofit

    override fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Result<Daily>> = flow {
        try {
            emit(Result.success(apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units).body()!!))
        } catch (e: IOException) {
            emit(Result.failure(Throwable("Network error: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)

    override fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Result<Hourly>> = flow {
        try {
            emit(Result.success(apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units).body()!!))
        } catch (e: IOException) {
            emit(Result.failure(Throwable("Network error: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)

    override fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Result<Daily>> = flow {
        try {
            emit(Result.success(apiService.getDailyForecastByCoordinates(lat, lon, 8, Secret.appId, Secret.units).body()!!))
        } catch (e: IOException) {
            emit(Result.failure(Throwable("Network error: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)


    override fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Result<Triple<Daily, Hourly, Daily>>> = flow {
        try {
            val currentWeather = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units).body()!!
            val hourlyForecast = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units).body()!!
            val dailyForecast = apiService.getDailyForecastByCoordinates(lat, lon, 7, Secret.appId, Secret.units).body()!!
            emit(Result.success(Triple(currentWeather, hourlyForecast, dailyForecast)))
        } catch (e: IOException) {
            emit(Result.failure(Throwable("Network error: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)
}



