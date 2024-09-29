package com.galal.weather.Retrofit

import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String ,
    ): Response<Daily>


    @GET("forecast/hourly")
    suspend fun getHourlyForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String

    ): Response<Hourly>

    @GET("forecast/daily")
    suspend fun getDailyForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") count: Int,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
    ): Response<Daily>


}


