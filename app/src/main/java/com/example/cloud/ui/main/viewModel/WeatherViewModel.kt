package com.galal.weather.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.repository.WeatherRepositoryImpl
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepositoryImpl) : ViewModel() {


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


