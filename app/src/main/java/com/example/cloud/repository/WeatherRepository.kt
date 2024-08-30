package com.example.cloud.repository

import android.util.Log
import com.galal.weather.Retrofit.ApiInterface
import com.galal.weather.Model.weatherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    suspend fun fetchWeather(cityName: String): Result<weatherApp> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getWeatherDate(cityName, "f14490483f30c01134e74a5e894e3a87", "metric")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving weather data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}

/* suspend fun fetchWeather(cityName: String): Result<weatherApp> {
       return withContext(Dispatchers.IO) {
           try {
               val response = retrofit.getWeatherDate(cityName, "9bda4264b961f7ebb7a9e7c8f689a549", "metric")
               if (response.isSuccessful && response.body() != null) {
                   Result.success(response.body()!!)
               } else {
                   // Log the error response for debugging
                   val errorBody = response.errorBody()?.string()
                   Log.e("WeatherRepository", "API Error: $errorBody")
                   Result.failure(Throwable("Error retrieving weather data: $errorBody"))
               }
           } catch (e: Exception) {
               Log.e("WeatherRepository", "Exception: ${e.message}")
               Result.failure(e)
           }
       }
   }*/