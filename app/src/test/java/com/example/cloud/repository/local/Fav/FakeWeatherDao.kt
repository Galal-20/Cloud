package com.example.cloud.repository.local.Fav

import com.example.cloud.database.dao.WeatherDao
import com.example.cloud.database.entity.CurrentWeatherEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeWeatherDao : WeatherDao {

    private val weatherData = mutableListOf<CurrentWeatherEntity>()
    private val weatherFlow = MutableStateFlow<List<CurrentWeatherEntity>>(weatherData)

    override suspend fun insertWeather(weather: CurrentWeatherEntity) {
        weatherData.add(weather)
        weatherFlow.emit(weatherData.toList())
    }

    override suspend fun deleteWeather(weather: CurrentWeatherEntity) {
        weatherData.remove(weather)
        weatherFlow.emit(weatherData.toList())
    }

    fun getWeatherFlow(): Flow<List<CurrentWeatherEntity>> = weatherFlow.asStateFlow()

    override suspend fun getWeatherByCity(city: String): CurrentWeatherEntity? {
        return weatherData.find { it.city == city }
    }
    override fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>> {
        return weatherFlow.asStateFlow()
    }

    override suspend fun getFirstWeatherItem(): CurrentWeatherEntity? {
        return weatherData.firstOrNull()
    }
}