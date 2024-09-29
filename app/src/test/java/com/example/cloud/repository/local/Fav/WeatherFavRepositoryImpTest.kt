package com.example.cloud.repository.local.Fav

import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.ui.favourites.viewModel.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherFavRepositoryImpTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var repository: WeatherFavRepositoryImp
    private lateinit var fakeWeatherDao: FakeWeatherDao

    @Before
    fun setup() {
        fakeWeatherDao = FakeWeatherDao()
        repository = WeatherFavRepositoryImp(fakeWeatherDao)
    }

    @Test
    fun insertWeather_addsWeatherToRepository() = runTest {
        // Arrange
        val weatherEntity = CurrentWeatherEntity(
            lat = 40.7128,
            lon = -74.0060,
            city = "New York",
            temperature = 25.0,
            temperatureMin = 20.0,
            temperatureMax = 30.0,
            main = "Cloudy",
            windSpeed = 5.0,
            seaPressure = 1013,
            humidity = 60,
            sunset = 1600000000L,
            sunrise = 1599990000L,
            timestamp = System.currentTimeMillis(),
            clouds = 40,
            icon = "",
            date = 1000,
            day = "",
            id = 10,
            lottieAnimation = "",
            imageWeather = "",
            description = ""
        )

        // Act
        repository.insertWeather(weatherEntity)

        // Assert
        assertEquals(listOf(weatherEntity), fakeWeatherDao.getWeatherFlow().first())
    }

    @Test
    fun deleteWeather_removesWeatherFromRepository() = runTest {
        // Arrange
        val weather = CurrentWeatherEntity(
            lat = 40.7128,
            lon = -74.0060,
            city = "New York",
            temperature = 25.0,
            temperatureMin = 20.0,
            temperatureMax = 30.0,
            main = "Cloudy",
            windSpeed = 5.0,
            seaPressure = 1013,
            humidity = 60,
            sunset = 1600000000L,
            sunrise = 1599990000L,
            timestamp = System.currentTimeMillis(),
            clouds = 40,
            icon = "",
            date = 1000,
            day = "",
            id = 10,
            lottieAnimation = "",
            imageWeather = "",
            description = ""

        )
        repository.insertWeather(weather)

        // Act
        repository.deleteWeather(weather)

        // Assert
        assertEquals(emptyList<CurrentWeatherEntity>(), fakeWeatherDao.getWeatherFlow().first())
    }

    @Test
    fun getFirstWeather() = runTest {
        val weather = CurrentWeatherEntity(
            lat = 40.7128,
            lon = -74.0060,
            city = "New York",
            temperature = 25.0,
            temperatureMin = 20.0,
            temperatureMax = 30.0,
            main = "Cloudy",
            windSpeed = 5.0,
            seaPressure = 1013,
            humidity = 60,
            sunset = 1600000000L,
            sunrise = 1599990000L,
            timestamp = System.currentTimeMillis(),
            clouds = 40,
            icon = "",
            date = 1000,
            day = "",
            id = 10,
            lottieAnimation = "",
            imageWeather = "",
            description = ""
        )
        // Act
        repository.insertWeather(weather)

        // Assert
        assertEquals(weather, repository.getFirstWeatherItem())
    }
}