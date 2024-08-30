package com.galal.weather.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.repository.WeatherRepository
import com.galal.weather.Model.weatherApp
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<Result<weatherApp>>()
    val weatherData: LiveData<Result<weatherApp>> get() = _weatherData


    fun fetchWeather(cityName: String) {
        viewModelScope.launch {
            val result = repository.fetchWeather(cityName)
            _weatherData.postValue(result)

        }
    }

}
