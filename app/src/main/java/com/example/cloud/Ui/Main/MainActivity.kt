package com.example.cloud.Ui.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud.R
import com.example.cloud.Ui.Main.Adapter.DaysAdapter
import com.example.cloud.Ui.Main.Adapter.HoursAdapter
import com.example.cloud.Ui.Map.MapActivity
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.model.Daily
import com.example.cloud.model.HourlyWeather
import com.example.cloud.model.ListElement
import com.example.cloud.repository.WeatherRepository
import com.galal.weather.ViewModel.WeatherViewModel
import com.galal.weather.ViewModel.WeatherViewModelFactory
import com.google.android.material.snackbar.Snackbar
import mumayank.com.airlocationlibrary.AirLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), AirLocation.Callback {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var airLocation: AirLocation


    private var city: String =""
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(WeatherRepository())
    }
    private var dataFetched = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupObservers()
    }

    private fun setupObservers() {
        getLocation()

        weatherViewModel.weatherData.observe(this, Observer { result ->
            result.fold(
                onSuccess = { data ->
                    updateUI(data)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        })

        weatherViewModel.hourlyForecastData.observe(this, Observer { result ->
            result.fold(
                onSuccess = { hourlyForecastResponse ->
                    setupHourlyForecastRecyclerView(hourlyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        })

        weatherViewModel.dailyForecastData.observe(this, Observer { result ->
            result.fold(
                onSuccess = { dailyForecastResponse ->
                    setupDailyForecastRecyclerView(dailyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        })


    }


    private fun setupHourlyForecastRecyclerView(hourlyForecastList: List<HourlyWeather>) {
        val adapter = binding.hoursRecyclerView.adapter as? HoursAdapter ?: HoursAdapter()
        binding.hoursRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.hoursRecyclerView.adapter = adapter
        adapter.submitList(hourlyForecastList)
    }
    private fun setupDailyForecastRecyclerView(dailyForecastList: List<ListElement>) {
        Log.d("MainActivity", "Daily Forecast List: $dailyForecastList")
        val adapter = binding.dayRecyclerView.adapter as? DaysAdapter ?: DaysAdapter()
        binding.dayRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
            false)
        binding.dayRecyclerView.adapter = adapter
        adapter.submitList(dailyForecastList)
    }

    private fun updateUI(weather: Daily) {
        binding.temp.text = "${weather.main.temp}°C"
        binding.maxTemp.text = "${weather.main.temp_max}°C"
        binding.miniTemp.text = "${weather.main.temp_min}° / "
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = "${weather.wind.speed} m/s"
        binding.sunrisee.text = time(weather.sys.sunrise.toLong())
        binding.sunset.text = time(weather.sys.sunset.toLong())
        binding.sea.text = "${weather.main.pressure} hpa"

        val weatherCondition = weather.weather.firstOrNull()
        binding.condition.text = weatherCondition?.main ?: "Unknown"

        binding.day.text = dayName()
        binding.date.text = date()
        binding.textLocation.text = city
        binding.time.text = time(System.currentTimeMillis() / 1000)

        runOnUiThread {
            changeImageWeather(weatherCondition?.main ?: "unknown")
        }
    }


    private fun changeImageWeather(conditions: String) {
        when (conditions) {
            "Clouds", "Mist", "Foggy", "Overcast", "Partly Clouds" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear", "Sunny", "Clear Sky" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Heavy Rain", "Showers", "Moderate Rain", "Drizzle", "Light Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Heavy Snow", "Moderate Snow", "Blizzard", "Light Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.cancelAnimation()
        binding.lottieAnimationView.playAnimation()    }

    @SuppressLint("SimpleDateFormat")
    private fun date(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    private fun time(timesTemp: Long): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(Date(timesTemp * 1000))
    }

    fun dayName(): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }


    private fun getLocation() {
        airLocation = AirLocation(this, this, false, 0, "")
        airLocation.start()
    }
    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
        Snackbar.make(binding.sea, "Check your permission", Snackbar.LENGTH_SHORT).show()
    }
    override fun onSuccess(locations: ArrayList<Location>) {
        if (dataFetched) return

        locations[0].accuracy
        lat = locations[0].latitude
        lon = locations[0].longitude
        val geocoder = Geocoder(this)

        val address = geocoder.getFromLocation(lat, lon, 1)
        if (address != null) {
            city = address[0].adminArea+", "+address[0].countryName
            weatherViewModel.fetchWeather(city)
            weatherViewModel.fetchHourlyForecast(city)
            weatherViewModel.fetchDailyForecast(city)
            dataFetched = true

        }else{
            binding.textLocation.text = "Unknown"
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //**********************************************************************************************

    fun Notification(view: View) {}
    fun Favourites(view: View) {}
    fun openLocation(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)
        startActivity(intent)
    }
}










