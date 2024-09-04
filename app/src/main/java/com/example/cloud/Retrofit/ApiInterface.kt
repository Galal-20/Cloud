package com.galal.weather.Retrofit

import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    suspend fun getWeatherDate(
        @Query("q") city: String,
        @Query("appId") appId: String,
        @Query("units") units: String,
    ): Response<Daily>

    @GET("forecast/hourly")
    suspend fun getHourlyForecast(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Hourly>


    @GET("forecast/daily")
    suspend fun getDailyForecast(
        @Query("q") city: String,
        @Query("cnt") count: Int,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Daily>




}








