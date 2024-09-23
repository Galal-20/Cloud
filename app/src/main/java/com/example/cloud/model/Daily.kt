// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.example.cloud.model


data class Daily (
    val city: City,
    val cnt: Long,
    val cod: String,
    val message: Double,
    val list: List<ListElement>,
    val temp: Tempe,
    val base: String,
    val clouds: Clouds,
    val dt: Int,
    val main: Main,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind,
)

data class City (
    val country: String,
    val coord: Coord,
    val timezone: Long,
    val name: String,
    val id: Long,
    val population: Long


)

data class Coord (
    val lon: Double,
    val lat: Double
)

data class ListElement (

    val rain: Double? = null,
    val sunrise: Long,
    val temp: Tempe,
    val deg: Long,
    val pressure: Long,
    val clouds: Long,
    val feelsLike: FeelsLike,
    val speed: Double,
    val dt: Long,
    val pop: Double,
    val sunset: Long,
    val weather: List<Weather>,
    val humidity: Long,
    val gust: Double,

    val main: Main,
    val sys: Sys,
    val visibility: Int,
    val wind: Wind,
)

data class FeelsLike (
    val eve: Double,
    val night: Double,
    val day: Double,
    val morn: Double
)

data class Tempe (
    val min: Double,
    val max: Double,
    val eve: Double,
    val night: Double,
    val day: Double,
    val morn: Double
)

data class Weather (
    val icon: String,
    val description: String,
    val main: String,
    val id: Long
)


data class Clouds(
    val all: Int
)


data class Wind(
    val deg: Int,
    val speed: Double
)

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)




