package com.example.cloud.repository.repositoriesFile

import android.content.Context
import android.app.AlarmManager
import com.example.cloud.database.dao.AlarmDao
import com.example.cloud.database.dao.WeatherDao
import com.example.cloud.database.entity.AlarmEntity
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.repository.local.Alarm.AlarmRepository
import com.example.cloud.repository.local.Alarm.AlarmRepositoryImpl
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryInterface
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.repository.remote.WeatherRepositoryInterface
import kotlinx.coroutines.flow.Flow

class StoreRepositoryImp (
    private val weatherFavRepositoryInterface: WeatherFavRepositoryInterface,
    private val alarmRepository: AlarmRepository,
    private val weatherRemoteRepository: WeatherRepositoryInterface
) : StoreRepository {



    // Alarm methods
    override suspend fun setAlarm(timeInMillis: Long, alarmId: Int) {
        alarmRepository.setAlarm(timeInMillis, alarmId)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmRepository.deleteAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmRepository.getAllAlarms()
    }

    // Local Weather (Favorites) methods
    override suspend fun insertWeather(weather: CurrentWeatherEntity) {
        weatherFavRepositoryInterface.insertWeather(weather)
    }

    override fun getAllWeatherData(): Flow<List<CurrentWeatherEntity>> {
        return weatherFavRepositoryInterface.getAllWeatherData()
    }

    override suspend fun getFirstWeatherItem(): CurrentWeatherEntity? {
        return weatherFavRepositoryInterface.getFirstWeatherItem()
    }

    override suspend fun deleteWeather(weather: CurrentWeatherEntity) {
        weatherFavRepositoryInterface.deleteWeather(weather)
    }

    // Remote Weather methods
    override fun fetchWeatherByCoordinates(lat: Double, lon: Double): Flow<Daily> {
        return weatherRemoteRepository.fetchWeatherByCoordinates(lat, lon)
    }

    override fun fetchHourlyForecastByCoordinate(lat: Double, lon: Double): Flow<Hourly> {
        return weatherRemoteRepository.fetchHourlyForecastByCoordinate(lat, lon)
    }

    override fun fetchDailyForecastByCoordinate(lat: Double, lon: Double): Flow<Daily> {
        return weatherRemoteRepository.fetchDailyForecastByCoordinate(lat, lon)
    }

    override fun getWeatherDataForNotification(lat: Double, lon: Double): Flow<Triple<Daily, Hourly, Daily>> {
        return weatherRemoteRepository.getWeatherDataForNotification(lat, lon)
    }

}
