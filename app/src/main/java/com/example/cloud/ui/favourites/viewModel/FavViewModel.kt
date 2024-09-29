package com.example.cloud.ui.favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavViewModel(private val repository: WeatherFavRepositoryInterface) : ViewModel() {

    val allWeatherData: Flow<List<CurrentWeatherEntity>> = repository.getAllWeatherData()


   suspend fun deleteWeather(weather: CurrentWeatherEntity) {
        viewModelScope.launch {
            repository.deleteWeather(weather)
        }
    }

    suspend fun getFirstWeatherItem(): CurrentWeatherEntity? {
        return repository.getFirstWeatherItem()
    }


    suspend fun insertOrUpdateWeather(weather: CurrentWeatherEntity) {
        val existingWeather = repository.getWeatherByCity(weather.city)
        viewModelScope.launch {
            if (existingWeather != null) {
                repository.deleteWeather(existingWeather)
            }
            repository.insertWeather(weather)
        }
    }


}

