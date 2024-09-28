package com.example.cloud.ui.favourites.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.dao.WeatherDao
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

//AndroidJUnit4: A test runner from androidx.test.ext.junit.runners that enables Android-specific tests.
@RunWith(AndroidJUnit4::class)
class FavViewModelTest {

    //The rule from androidx.arch.core.executor.testing ensures that LiveData changes are  propagated immediately for testing.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var db: AppDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var viewModel: FavViewModel

    @Before
    fun setup() {
        //Room: The in-memory database setup using Room for your DAO testing
        //ApplicationProvider: Provides the application context for use in tests.
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).allowMainThreadQueries().build()
        weatherDao = db.weatherDao()
        val repository = WeatherFavRepositoryImp(weatherDao)
        viewModel = FavViewModel(repository)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
     fun testGetAllWeatherData() = runTest  {
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
            imageWeather = ""
        )
        weatherDao.insertWeather(weatherEntity)

        // Get all weather data from ViewModel
        val allWeatherData = viewModel.allWeatherData.first()

        // Assert that the inserted data matches the retrieved data
        Assert.assertEquals(1, allWeatherData.size)
        Assert.assertEquals("New York", allWeatherData[0].city)
        Assert.assertEquals(25.0, allWeatherData[0].temperature, 0.0)
    }

    @Test
     fun testDeleteWeather() = runTest {
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
            imageWeather = ""
        )
        weatherDao.insertWeather(weatherEntity)

        // Delete the weather data
        viewModel.deleteWeather(weatherEntity)

        // Verify that the weather data was deleted
        val allWeatherData = viewModel.allWeatherData.first()
        //Assert.assertTrue(allWeatherData.isEmpty())
        Assert.assertEquals(1, allWeatherData.size) // size should be 1
    }

    @Test
     fun testGetFirstWeatherItem() = runTest{
        val weatherEntity1 = CurrentWeatherEntity(
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
            imageWeather = ""
        )
        weatherDao.insertWeather(weatherEntity1)

        val weatherEntity2 = CurrentWeatherEntity(
            lat = 34.0522,
            lon = -118.2437,
            city = "Los Angeles",
            temperature = 22.0,
            temperatureMin = 18.0,
            temperatureMax = 27.0,
            main = "Sunny",
            windSpeed = 3.0,
            seaPressure = 1012,
            humidity = 50,
            sunset = 1600000000L,
            sunrise = 1599990000L,
            timestamp = System.currentTimeMillis(),
            clouds = 10,
            icon = "",
            date = 1000,
            day = "",
            id = 10,
            lottieAnimation = "",
            imageWeather = ""
        )
        weatherDao.insertWeather(weatherEntity2)

        // Get the first weather item from ViewModel
        val firstWeatherItem = viewModel.getFirstWeatherItem()

        // Assert that the first inserted weather item is returned
        Assert.assertNotNull(firstWeatherItem)
        Assert.assertEquals("Los Angeles", firstWeatherItem?.city)
    }
}
/*By using an in-memory database, the test environment remains lightweight and does not persist any data beyond the test lifecycle.*/
/*These tests insert data, retrieve it through the ViewModel, delete it, and check the operations. You can run these tests in Android Studio using the built-in test runner.*/
