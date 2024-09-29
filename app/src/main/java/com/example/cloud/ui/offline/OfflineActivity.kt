package com.example.cloud.ui.offline

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cloud.R
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.databinding.ActivityOfflineBinding
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import com.example.cloud.ui.favourites.viewModel.FavViewModel
import com.example.cloud.ui.favourites.viewModel.FavViewModelFactory
import com.example.cloud.ui.main.view.MainActivity
import com.example.cloud.ui.splash.Splash
import com.example.cloud.utils.Settings.date
import com.example.cloud.utils.Settings.dayName
import com.example.cloud.utils.Settings.getUnitSymbol
import com.example.cloud.utils.Settings.getWindSpeedUnitSymbol
import com.example.cloud.utils.Settings.time
import com.example.cloud.utils.network.Check_Network
import com.example.cloud.utils.network.NetworkChangeReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class OfflineActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {
    private val binding: ActivityOfflineBinding by lazy {
        ActivityOfflineBinding.inflate(layoutInflater)
    }

    private val handle = Handler(Looper.getMainLooper())
    private val updateAnimation = object : Runnable {
        override fun run() {
            binding.lottieAnimationView.playAnimation()
            handle.postDelayed(this, 2000)
        }
    }
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private lateinit var checkNetwork: Check_Network
    private lateinit var pascalUnit:String
    private var pressureValue :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkNetwork = Check_Network(this, findViewById(android.R.id.content))
        networkChangeReceiver = NetworkChangeReceiver(this)
        handle.post(updateAnimation)

        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val temperatureMax = intent.getDoubleExtra("temperatureMax", 0.0)
        val temperatureMin = intent.getDoubleExtra("temperatureMin", 0.0)
        val humidity = intent.getIntExtra("humidity", 0)
        val windSpeed = intent.getDoubleExtra("windSpeed", 0.0)
        val seaPressure = intent.getIntExtra("seaPressure", 0)
        val sunrise = intent.getLongExtra("sunrise", 0L)
        val sunset = intent.getLongExtra("sunset", 0L)
        val description = intent.getStringExtra("description") ?: ""
        val clouds = intent.getIntExtra("clouds", 0)
        val imageWeather = intent.getStringExtra("imageWeather") ?: ""
        val city = intent.getStringExtra("city") ?: ""

        val weatherEntity = CurrentWeatherEntity(
            lat = 0.0,
            lon = 0.0,
            city = city,
            temperature = temperature,
            temperatureMin = temperatureMin,
            temperatureMax = temperatureMax,
            main = "",
            windSpeed = windSpeed,
            seaPressure = seaPressure,
            humidity = humidity,
            sunset = sunset,
            sunrise = sunrise,
            timestamp = System.currentTimeMillis(),
            clouds = clouds,
            icon = "",
            date = 0,
            day = "",
            id = 10,
            lottieAnimation = "",
            imageWeather = imageWeather,
            description = description
        )

        showWeatherData(weatherEntity)
    }
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showWeatherData(weatherEntity: CurrentWeatherEntity) {
        binding.temp.text = String.format("%.0f°%s", weatherEntity.temperature, getUnitSymbol(R.string.celsius.toString()))
        binding.maxTemp.text = String.format("%.0f°%s", weatherEntity.temperatureMax, getUnitSymbol(R.string.celsius.toString()))
        binding.miniTemp.text = String.format("%.0f°%s/", weatherEntity.temperatureMin, getUnitSymbol(R.string.celsius.toString()))
        binding.humidity.text = "${weatherEntity.humidity} %"
        binding.windSpeed.text = String.format(
            Locale.getDefault(),
            "%.0f %s", weatherEntity.windSpeed,
            getWindSpeedUnitSymbol(R.string.meter_second.toString())
        )
        binding.sunriseText.text = time(weatherEntity.sunrise)
        binding.sunsetText.text = time(weatherEntity.sunset)
        pascalUnit = binding.root.context.getString(R.string.pascal)
        pressureValue = weatherEntity.seaPressure
        binding.sea.text = "$pressureValue $pascalUnit"
        binding.today.text = weatherEntity.description
        binding.condition.text = weatherEntity.clouds.toString() + getString(R.string.percentage)
        changeImageWeatherS(weatherEntity.imageWeather)
        binding.cityName.text = weatherEntity.city
        binding.date.text = date()
        binding.day.text = dayName()

    }
    private fun changeImageWeatherS(imageWeather: String) {
        when (imageWeather) {
            "cloud_background" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "sunny_background" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "rain_background" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "snow_background" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
    }




    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkChangeReceiver?.let {
            registerReceiver(it, filter)
        }
    }
    override fun onPause() {
        super.onPause()
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
    }
    override fun onNetworkChange(isConnected: Boolean) {
        checkNetwork.onNetworkChange(isConnected)
        if (isConnected) {
            //Toast.makeText(this, "Internet is available", Toast.LENGTH_SHORT).show()
        }else{
            //Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show()
        }
    }

}