package com.example.cloud.repository.remote

import android.content.Context
import android.util.Log
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.retrofit.ApiClient
import com.example.cloud.utils.PreferencesUtils
import com.example.cloud.utils.Secret
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(private val context: Context) : WeatherRepositoryInterface {

    private val apiService = ApiClient.retrofit

    override fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Daily> = flow {
        val lang = PreferencesUtils.getLanguage(context)
        Log.d("lang", "Fetching weather data for coordinates: $lang")

        val response = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret.units,
            lang)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
            Log.d("lang", "Response body: ${response.body()}")
        } else {
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Hourly> = flow {
        val response = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId, Secret
            .units)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }


    override fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Daily> = flow {
        val lang = PreferencesUtils.getLanguage(context)
        val response = apiService.getDailyForecastByCoordinates(lat, lon, 8, Secret.appId, Secret
            .units, lang)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }

    override fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Triple<Daily, Hourly, Daily>> = flow {
        val lang = PreferencesUtils.getLanguage(context)

        val currentWeather = apiService.getWeatherByCoordinates(lat, lon, Secret.appId, Secret
            .units, lang)
        val hourlyForecast = apiService.getHourlyForecastByCoordinates(lat, lon, Secret.appId,
            Secret.units )
        val dailyForecast = apiService.getDailyForecastByCoordinates(lat, lon, 7, Secret.appId,
            Secret.units,lang)
        if (currentWeather.isSuccessful && hourlyForecast.isSuccessful && dailyForecast
            .isSuccessful){
            emit(Triple(currentWeather.body()!!, hourlyForecast.body()!!, dailyForecast.body()!!))
        }else{
            throw Throwable("Error retrieving weather data")
        }
    }



}


