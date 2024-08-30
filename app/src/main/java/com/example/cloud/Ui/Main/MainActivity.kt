package com.example.cloud.Ui.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud.R
import com.example.cloud.Ui.Map.MapActivity
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.repository.WeatherRepository
import com.galal.weather.Model.weatherApp
import com.galal.weather.ViewModel.WeatherViewModel
import com.galal.weather.ViewModel.WeatherViewModelFactory
import com.google.android.material.snackbar.Snackbar
import mumayank.com.airlocationlibrary.AirLocation
import org.osmdroid.views.overlay.Polygon.OnClickListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        getLocation()

        setupObservers()


    }

    // Get location and set current weather

    private fun setupObservers() {
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
    }

    private fun updateUI(weather: weatherApp) {
        binding.weather.text = weather.weather.firstOrNull()?.main ?: "Unknown"
        binding.temp.text = "${weather.main.temp}°C"
        binding.maxTemp.text = "Max: ${weather.main.temp_max}°C"
        binding.miniTemp.text = "Min: ${weather.main.temp_min}°C"
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = "${weather.wind.speed} m/s"
        binding.sunrisee.text = time(weather.sys.sunrise.toLong())
        binding.sunset.text = time(weather.sys.sunset.toLong())
        binding.sea.text = "${weather.main.pressure} hpa"
        binding.condition.text = weather.weather.firstOrNull()?.main ?: "Unknown"
        binding.day.text = dayName()
        binding.date.text = date()
        binding.textLocation.text = city

        binding.time.text = time(System.currentTimeMillis() / 1000) // Using current time



        changeImageWeather(weather.weather.firstOrNull()?.main ?: "unknown")
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
        binding.lottieAnimationView.playAnimation()
    }

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
        locations[0].accuracy
        lat = locations[0].latitude
        lon = locations[0].longitude
        val geocoder = Geocoder(this)

        val address = geocoder.getFromLocation(lat, lon, 1)
        if (address != null) {
            city = address[0].adminArea+", "+address[0].countryName
            weatherViewModel.fetchWeather(city)
        }else{
            binding.textLocation.text = "Unknown"
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data) // ADD THIS LINE INSIDE onActivityResult
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults) // ADD THIS LINE INSIDE onRequestPermissionResult
    }

    //**********************************************************************************************

    // Recycler View to get weather for next 16 hours





    // **********************************************************************************************

    //Navigation(Notification,Favourites,Location)

    fun Notification(view: View) {}
    fun Favourites(view: View) {}
    fun openLocation(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)
        startActivity(intent)
    }
}



