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

    override fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Daily> = flow {
        val response = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Hourly> = flow {
        val response = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }


    override fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Daily> = flow {
        val response = apiService.getDailyForecastByCoordinates(lat, lon, 8, Secret.appId, Secret.units)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Triple<Daily, Hourly, Daily>> = flow {
        val currentWeather = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units)
        val hourlyForecast = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret.units)
        val dailyForecast = apiService.getDailyForecastByCoordinates(lat, lon, 7, Secret.appId, Secret.units)
        if (currentWeather.isSuccessful && hourlyForecast.isSuccessful && dailyForecast
            .isSuccessful){
            emit(Triple(currentWeather.body()!!, hourlyForecast.body()!!, dailyForecast.body()!!))
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }



}


