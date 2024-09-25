package com.galal.weather.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.repository.remote.WeatherRepositoryInterface
import com.example.cloud.retrofit.ApiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepositoryImpl) : ViewModel() {

    private val _weatherDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDataStateFlow: StateFlow<ApiState> get() = _weatherDataStateFlow

    private val _hourlyForecastDataByCoordinates = MutableStateFlow<ApiState>(ApiState.Loading)
    val hourlyForecastDataByCoordinates: StateFlow<ApiState> get() = _hourlyForecastDataByCoordinates

    private val _dailyForecastDataByCoordinates = MutableStateFlow<ApiState>(ApiState.Loading)
    val dailyForecastDataByCoordinates: StateFlow<ApiState> get() = _dailyForecastDataByCoordinates


    private val _notificationWeatherDataStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val notificationWeatherDataStateFlow: StateFlow<ApiState> get() = _notificationWeatherDataStateFlow


    fun fetchCurrentWeatherDataByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherDataStateFlow.value = ApiState.Loading
            repository.fetchWeatherByCoordinates(lat, lon)
                .catch {
                _weatherDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { weather ->
                _weatherDataStateFlow.value = ApiState.Success(weather)
            }
        }
    }


    fun fetchHourlyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _hourlyForecastDataByCoordinates.value = ApiState.Loading
            repository.fetchHourlyForecastByCoordinate(lat, lon).catch {
                _weatherDataStateFlow.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect { hourlyForecast ->
                    _hourlyForecastDataByCoordinates.value = ApiState.Success(hourlyForecast)
                }
        }
    }



    fun fetchDailyWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _dailyForecastDataByCoordinates.value = ApiState.Loading
            repository.fetchDailyForecastByCoordinate(lat, lon)
                .catch {
                    _dailyForecastDataByCoordinates.value = ApiState.Failure(it.message ?: "Unknown Error")
                }
                .collect{ dailyForecast ->
                    _dailyForecastDataByCoordinates.value = ApiState.Success(dailyForecast)
                }
        }
    }

    fun fetchWeatherDataForNotification(lat: Double, lon: Double) {
        viewModelScope.launch {
            _notificationWeatherDataStateFlow.value = ApiState.Loading
            repository.getWeatherDataForNotification(lat, lon)
                .catch { exception ->
                    _notificationWeatherDataStateFlow.value = ApiState.Failure(exception.message ?: "Unknown Error")
                }
                .collect { weatherData ->
                    _notificationWeatherDataStateFlow.value = ApiState.Success(weatherData)
                }
        }
    }
}


