package com.example.cloud.utils.room

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cloud.R
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import com.example.cloud.ui.favourites.viewModel.FavViewModel
import com.example.cloud.ui.favourites.viewModel.FavViewModelFactory
import com.example.cloud.utils.Settings.dayName
import com.galal.weather.ViewModel.WeatherViewModel
import kotlinx.coroutines.launch

object WeatherUtils {

    fun saveWeatherToDatabase(
        context: Context,
        weatherViewModel: WeatherViewModel,
        lat: Double,
        lon: Double,
        city: String
    ) {
        weatherViewModel.weatherDataByCoordinates.value?.let { result ->
            result.fold(
                onSuccess = { weather ->
                    val weatherCondition = weather.weather.firstOrNull()?.main ?: "Unknown"
                    val imageWeather = getImageWeatherForCondition(weatherCondition)
                    val weatherEntity = CurrentWeatherEntity(
                        lat = lat,
                        lon = lon,
                        city = city,
                        day = dayName(),
                        temperature = weather.main.temp,
                        main = weatherCondition,
                        icon = weather.weather.firstOrNull()?.icon ?: "",
                        date = System.currentTimeMillis(),
                        temperatureMin = weather.main.temp_min,
                        temperatureMax = weather.main.temp_max,
                        timestamp = System.currentTimeMillis(),
                        windSpeed = weather.wind.speed,
                        seaPressure = weather.main.pressure,
                        sunset = weather.sys.sunset.toLong(),
                        sunrise = weather.sys.sunrise.toLong(),
                        humidity = weather.main.humidity,
                        lottieAnimation = getLottieAnimationForCondition(weatherCondition),
                        imageWeather = imageWeather,
                        clouds = weather.clouds.all
                    )

                    val repository = WeatherFavRepositoryImp(AppDatabase.getDatabase(context).weatherDao())
                    val viewModel = ViewModelProvider(
                        context as AppCompatActivity,
                        FavViewModelFactory(repository)
                    )[FavViewModel::class.java]

                    context.lifecycleScope.launch {
                        try {
                            viewModel.insertWeather(weatherEntity)
                            Toast.makeText(context, R.string.weather_saved, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onFailure = {
                    Toast.makeText(context, R.string.failed_to_retriev.toString(), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    private fun getImageWeatherForCondition(condition: String): String {
        return when (condition) {
            "Clouds", "Mist", "Foggy", "Overcast", "Partly Clouds" ,"Snow" -> "cloud_background"
            "Clear", "Sunny", "Clear Sky" -> "sunny_background"
            "Heavy Rain", "Showers", "Moderate Rain", "Drizzle", "Light Rain" -> "rain_background"
            "Heavy Snow", "Moderate Snow", "Blizzard", "Light Snow" -> "snow_background"
            else -> "sunny_background"
        }
    }

    private fun getLottieAnimationForCondition(condition: String): String {
        return when (condition) {
            "Clouds" -> "cloud_animation.json"
            "Clear" -> "sun_animation.json"
            "Rain" -> "rain_animation.json"
            "Snow" -> "snow_animation.json"
            else -> "default_animation.json"
        }
    }
}


