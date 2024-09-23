package com.example.cloud.repository.local.Fav


import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.database.dao.WeatherDao
import kotlinx.coroutines.flow.Flow

class WeatherFavRepositoryImp(private val weatherDao: WeatherDao): WeatherFavRepositoryInterface {

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
}


