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
import com.example.cloud.retrofit.ApiState
import com.example.cloud.ui.favourites.viewModel.FavViewModel
import com.example.cloud.ui.favourites.viewModel.FavViewModelFactory
import com.example.cloud.utils.Settings.dayName
import com.galal.weather.ViewModel.WeatherViewModel
import kotlinx.coroutines.launch

object WeatherUtils {

    fun saveWeatherToDatabase(context: Context, weatherViewModel: WeatherViewModel, lat: Double, lon: Double, city: String) {
        val state = weatherViewModel.weatherDataStateFlow.value
        if (state is ApiState.Success) {
            val weather = state.data as com.example.cloud.model.Daily

            val weatherCondition = weather.weather.firstOrNull()?.description ?: "Unknown"
            val imageWeather = getImageWeatherForCondition(weatherCondition)

            val weatherEntity = CurrentWeatherEntity(
                lat = lat,
                lon = lon,
                city = city,
                day = dayName(),
                temperature = weather.main.temp,
                description = weatherCondition,
                main = weather.weather.firstOrNull()?.main ?: "",
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
                    viewModel.insertOrUpdateWeather(weatherEntity)
                    //Toast.makeText(context, R.string.weather_saved, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                }
            }
        } else if (state is ApiState.Failure) {
            Toast.makeText(context, R.string.failed_to_retriev, Toast.LENGTH_SHORT).show()
        }
    }




    private fun getImageWeatherForCondition(condition: String): String {
        return when (condition) {
            "Clouds","overcast clouds", "Mist", "Foggy",
            "Overcast", "Partly Clouds" ,"Snow" , "scattered clouds", "broken clouds", "few clouds"
            -> "cloud_background"
            "Clear", "Sunny", "Clear Sky","Sky Is Clear",
            -> "sunny_background"
            "Heavy Rain", "Showers","Rain", "Moderate Rain",
            "Drizzle", "Light Rain" ,"light rain","moderate rain"
            -> "rain_background"
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


