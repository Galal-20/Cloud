package com.example.cloud.ui.favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavViewModel(private val repository: WeatherFavRepositoryInterface) : ViewModel() {

    val allWeatherData: Flow<List<CurrentWeatherEntity>> = repository.getAllWeatherData()

    suspend fun insertWeather(weather: CurrentWeatherEntity) {
        viewModelScope.launch {
            repository.insertWeather(weather)
        }
    }

   suspend fun deleteWeather(weather: CurrentWeatherEntity) {
        viewModelScope.launch {
            repository.deleteWeather(weather)
        }
    }

    suspend fun getFirstWeatherItem(): CurrentWeatherEntity? {
        return repository.getFirstWeatherItem()
    }



}