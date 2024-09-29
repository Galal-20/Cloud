package com.example.cloud.ui.favourites.viewModel

import com.example.cloud.database.dao.WeatherDao
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryInterface
import kotlinx.coroutines.flow.Flow

class FakeWeatherFavRepository(private val weatherDao: WeatherDao): WeatherFavRepositoryInterface {
    override suspend fun insertWeather(weather: CurrentWeatherEntity) {
        weatherDao.insertWeather(weather)
    }

    override fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>> {
        return weatherDao.getAllWeatherData()
    }

    override suspend fun getFirstWeatherItem(): CurrentWeatherEntity? {
        return weatherDao.getFirstWeatherItem()
    }

    override suspend fun deleteWeather(weather: CurrentWeatherEntity) {
        weatherDao.deleteWeather(weather)
    }

    override suspend fun getWeatherByCity(city: String): CurrentWeatherEntity? {
        return weatherDao.getWeatherByCity(city)
    }
}