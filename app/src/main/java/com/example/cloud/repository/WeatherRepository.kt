package com.example.cloud.repository

import android.util.Log
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.galal.weather.Retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pro.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    suspend fun fetchWeatherByCoordinates(lat: Double, lon: Double): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getWeatherByCoordinates(lat, lon, "6a482dc37ff81d4d3deec39521543316", "metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Weather fetched successfully by coordinates: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving weather data by coordinates"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Result<Hourly> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getHourlyForecastByCoordinates(lat, lon,
                    "6a482dc37ff81d4d3deec39521543316",
                    "metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Hourly forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving hourly forecast data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getDailyForecastByCoordinates(lat, lon, 7,
                    "6a482dc37ff81d4d3deec39521543316","metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Daily forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving daily forecast data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getWeatherDataForNotification(lat: Double, lon: Double): Result<Triple<Daily, Hourly,
            Daily>> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch current weather
                val currentWeatherResponse = retrofit.getWeatherByCoordinates(lat, lon, "6a482dc37ff81d4d3deec39521543316", "metric")

                // Fetch hourly forecast
                val hourlyForecastResponse = retrofit.getHourlyForecastByCoordinates(lat, lon, "6a482dc37ff81d4d3deec39521543316", "metric")

                // Fetch daily forecast
                val dailyForecastResponse = retrofit.getDailyForecastByCoordinates(lat, lon, 7, "6a482dc37ff81d4d3deec39521543316", "metric")

                if (currentWeatherResponse.isSuccessful && hourlyForecastResponse.isSuccessful && dailyForecastResponse.isSuccessful &&
                    currentWeatherResponse.body() != null && hourlyForecastResponse.body() != null && dailyForecastResponse.body() != null) {

                    Log.d("WeatherRepository", "Weather data for notifications fetched successfully")
                    Result.success(Triple(currentWeatherResponse.body()!!, hourlyForecastResponse.body()!!, dailyForecastResponse.body()!!))
                } else {
                    Result.failure(Throwable("Error retrieving weather data for notifications"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}





/* suspend fun fetchWeather(cityName: String): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getWeatherDate(cityName, "6a482dc37ff81d4d3deec39521543316", "metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Current forecast fetched successfully: ${response
                        .body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving weather data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchHourlyForecast(cityName: String): Result<Hourly> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getHourlyForecast(cityName,
                    "6a482dc37ff81d4d3deec39521543316",
                    "metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Hourly forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving hourly forecast data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun fetchDailyForecast(cityName: String): Result<Daily> {
        return withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getDailyForecast(cityName, 7,
                    "6a482dc37ff81d4d3deec39521543316","metric")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherRepository", "Daily forecast fetched successfully: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Throwable("Error retrieving daily forecast data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
*/






