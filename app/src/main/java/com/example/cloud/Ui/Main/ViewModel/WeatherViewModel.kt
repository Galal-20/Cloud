package com.galal.weather.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.model.Daily
import com.example.cloud.model.HourlyForecastResponse
import com.example.cloud.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<Result<Daily>>()
    val weatherData: LiveData<Result<Daily>> get() = _weatherData

    private val _hourlyForecastData = MutableLiveData<Result<HourlyForecastResponse>>()
    val hourlyForecastData: LiveData<Result<HourlyForecastResponse>> get() = _hourlyForecastData

    private val _dailyForecastData = MutableLiveData<Result<Daily>>()
    val dailyForecastData: LiveData<Result<Daily>> get() = _dailyForecastData



    fun fetchWeather(cityName: String) {
        viewModelScope.launch {
            val result = repository.fetchWeather(cityName)
            _weatherData.postValue(result)

        }
    }

    fun fetchHourlyForecast(cityName: String) {
        viewModelScope.launch {
            val result = repository.fetchHourlyForecast(cityName)
            _hourlyForecastData.postValue(result)
        }
    }
    fun fetchDailyForecast(cityName: String) {
        viewModelScope.launch {
            val result = repository.fetchDailyForecast(cityName)
            _dailyForecastData.postValue(result)
        }
    }


}


