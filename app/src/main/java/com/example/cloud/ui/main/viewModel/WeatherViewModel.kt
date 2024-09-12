package com.galal.weather.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {


    private val _weatherDataByCoordinates = MutableLiveData<Result<Daily>>()
    val weatherDataByCoordinates: LiveData<Result<Daily>> get() = _weatherDataByCoordinates

    private val _hourlyForecastDataByCoordinates = MutableLiveData<Result<Hourly>>()
    val hourlyForecastDataByCoordinates: LiveData<Result<Hourly>> get() = _hourlyForecastDataByCoordinates

    private val _dailyForecastDataByCoordinates = MutableLiveData<Result<Daily>>()
    val dailyForecastDataByCoordinates: LiveData<Result<Daily>> get() = _dailyForecastDataByCoordinates


    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            val result = repository.fetchWeatherByCoordinates(lat, lon)
            _weatherDataByCoordinates.postValue(result)
        }
    }

    fun fetchHourlyWeatherByCoordinates(lat:Double, lon: Double){
        viewModelScope.launch {
            val result = repository.fetchHourlyForecastByCoordinate(lat, lon)
            _hourlyForecastDataByCoordinates.postValue(result)
        }
    }

    fun fetchDailyWeatherByCoordinates(lat:Double, lon: Double){
        viewModelScope.launch {
            val result = repository.fetchDailyForecastByCoordinate(lat, lon)
            _dailyForecastDataByCoordinates.postValue(result)
        }
    }

}



/* fun fetchWeather(cityName: String) {
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
    }*/

/* private val _weatherData = MutableLiveData<Result<Daily>>()
 val weatherData: LiveData<Result<Daily>> get() = _weatherData

 private val _hourlyForecastData = MutableLiveData<Result<Hourly>>()
 val hourlyForecastData: LiveData<Result<Hourly>> get() = _hourlyForecastData

 private val _dailyForecastData = MutableLiveData<Result<Daily>>()
 val dailyForecastData: LiveData<Result<Daily>> get() = _dailyForecastData*/



