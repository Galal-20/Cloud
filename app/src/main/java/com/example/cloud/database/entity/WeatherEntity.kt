package com.example.cloud.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val city: String,
    val day: String,
    val temperature: Double,
    val main: String?,
    val icon: String,
    val date: Long,
    val temperatureMin: Double,
    val temperatureMax: Double,
    val timestamp: Long,
    val lat: Double,
    val lon: Double,
    val windSpeed: Double,
    val seaPressure: Int,
    val sunset: Long,
    val sunrise: Long,
    val humidity: Int,
    val lottieAnimation: String,
    val imageWeather: String, // Added this field
    val clouds: Int
)


