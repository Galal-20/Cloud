package com.example.cloud.model



data class HourlyForecastResponse(
    val list: List<HourlyWeather>
)

data class HourlyWeather(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)


