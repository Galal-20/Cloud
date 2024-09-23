package com.galal.weather.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepositoryImpl) : ViewModel() {

    private val _weatherDataByCoordinates = MutableStateFlow<Result<Daily>?>(null)
    val weatherDataByCoordinates: StateFlow<Result<Daily>?> get() = _weatherDataByCoordinates

    private val _hourlyForecastDataByCoordinates = MutableStateFlow<Result<Hourly>?>(null)
    val hourlyForecastDataByCoordinates: StateFlow<Result<Hourly>?> get() = _hourlyForecastDataByCoordinates

    private val _dailyForecastDataByCoordinates = MutableStateFlow<Result<Daily>?>(null)
    val dailyForecastDataByCoordinates: StateFlow<Result<Daily>?> get() = _dailyForecastDataByCoordinates

    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchWeatherByCoordinates(lat, lon)
                .collect { result ->
                    _weatherDataByCoordinates.value = result
                }
        }
    }

    fun fetchHourlyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchHourlyForecastByCoordinate(lat, lon)
                .collect { result ->
                    _hourlyForecastDataByCoordinates.value = result
                }
        }
    }

    fun fetchDailyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchDailyForecastByCoordinate(lat, lon)
                .collect { result ->
                    _dailyForecastDataByCoordinates.value = result
                }
        }
    }
}


