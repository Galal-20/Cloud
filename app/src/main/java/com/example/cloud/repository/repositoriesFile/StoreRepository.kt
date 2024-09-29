package com.example.cloud.repository.repositoriesFile


import com.example.cloud.database.entity.AlarmEntity
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    // Alarm methods
    suspend fun setAlarm(timeInMillis: Long, alarmId: Int)
    suspend fun deleteAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    // Local Weather (Favorites) methods
    suspend fun insertWeather(weather: CurrentWeatherEntity)
    fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>>
    suspend fun getFirstWeatherItem(): CurrentWeatherEntity?
    suspend fun deleteWeather(weather: CurrentWeatherEntity)

    // Remote Weather methods
    fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Daily>
    fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Hourly>
    fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Daily>
    fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Triple<Daily, Hourly, Daily>>
}