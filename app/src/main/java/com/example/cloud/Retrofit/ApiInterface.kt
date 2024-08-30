package com.galal.weather.Retrofit

import com.galal.weather.Model.weatherApp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    suspend fun getWeatherDate(
        @Query("q") city: String,
        @Query("appId") appId: String,
        @Query("units") units: String
    ): Response<weatherApp>


   /* @GET("onecall")
    suspend fun getHourlyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = "current,minutely,daily,alerts"
    ): Response<HourlyWeatherResponse>*/
}
