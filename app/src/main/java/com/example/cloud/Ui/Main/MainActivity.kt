package com.example.cloud.Ui.Main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud.R
import com.example.cloud.Ui.Main.Adapter.DaysAdapter
import com.example.cloud.Ui.Main.Adapter.HoursAdapter
import com.example.cloud.Ui.Map.MapActivity
import com.example.cloud.Ui.Settings.SettingsBottomSheetDialog
import com.example.cloud.Ui.Splash.Splash
import com.example.cloud.Utils.NetworkChangeReceiver
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.model.Daily
import com.example.cloud.model.HourlyListElement
import com.example.cloud.model.ListElement
import com.example.cloud.repository.WeatherRepository
import com.galal.weather.ViewModel.WeatherViewModel
import com.galal.weather.ViewModel.WeatherViewModelFactory
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.snackbar.Snackbar
import mumayank.com.airlocationlibrary.AirLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //private lateinit var airLocation: AirLocation
    private lateinit var sharedPreferences: SharedPreferences
    private var city: String =""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var wasConnected = false
    private val weatherViewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(WeatherRepository()) }

    private val handle = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime()
            handle.postDelayed(this, 1000)
        }
    }
    private val updateAnimation = object : Runnable {
        override fun run() {
            binding.lottieAnimationView.playAnimation()
            handle.postDelayed(this, 2000)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkNetworkStatus()


        /*if (isFirstTimeOpen()) {
            showUserGuide()  // Show the guide if the app is opened for the first time
        }*/


        lat = intent.getDoubleExtra("latitude", 0.0)
        lon = intent.getDoubleExtra("longitude", 0.0)
        city = intent.getStringExtra("city") ?: "Unknown"


        setupObservers()
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en")
        setLocale(language)
        handle.post(updateTimeRunnable)
        handle.post(updateAnimation)
        binding.date.text = date()

        if (city != "Unknown") {
            weatherViewModel.fetchWeather(city)
            weatherViewModel.fetchHourlyForecast(city)
            weatherViewModel.fetchDailyForecast(city)
        } else {
            weatherViewModel.fetchWeatherByCoordinates(lat, lon)
            weatherViewModel.fetchHourlyWeatherByCoordinates(lat, lon)
            weatherViewModel.fetchDailyWeatherByCoordinates(lat, lon)
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            city = addresses?.get(0)?.locality + ", " + addresses?.get(0)?.countryName

        }

    }



    //**********************************************************************************************
    // Observer

    private fun setupObservers() {

        weatherViewModel.weatherData.observe(this) { result ->
            result.fold(
                onSuccess = { data ->
                    updateUI(data)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        weatherViewModel.hourlyForecastData.observe(this) { result ->
            result.fold(
                onSuccess = { hourlyForecastResponse ->
                    setupHourlyForecastRecyclerView(hourlyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        weatherViewModel.dailyForecastData.observe(this) { result ->
            result.fold(
                onSuccess = { dailyForecastResponse ->
                    setupDailyForecastRecyclerView(dailyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        //fetched by coordinates
        weatherViewModel.weatherDataByCoordinates.observe(this) { result ->
            result.fold(
                onSuccess = { data ->
                    updateUI(data)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }
        weatherViewModel.hourlyForecastDataByCoordinates.observe(this){ result ->
            result.fold(
                onSuccess = { hourlyForecastResponse ->
                    setupHourlyForecastRecyclerView(hourlyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        weatherViewModel.dailyForecastDataByCoordinates.observe(this){ result ->
            result.fold(
                onSuccess = { dailyForecastResponse ->
                    setupDailyForecastRecyclerView(dailyForecastResponse.list)
                },
                onFailure = {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            )
        }



    }

    //**********************************************************************************************
    // Recycler View

    private fun setupHourlyForecastRecyclerView(hourlyForecastList: List<HourlyListElement>) {
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
    //**********************************************************************************************

    // update Ui.

    private fun updateUI(weather: Daily) {
        val unit = sharedPreferences.getString("temperature_unit", "Celsius") ?: "Celsius"
        val windSpeedUnit = sharedPreferences.getString("wind_speed_unit", "Meter/Second") ?: "Meter/Second"

        val currentTemp = convertTemperature(weather.main.temp, unit)
        val maxTemp = convertTemperature(weather.main.temp_max, unit)
        val minTemp = convertTemperature(weather.main.temp_min, unit)
        val windSpeed = convertWindSpeed(weather.wind.speed, "Meter/Second", windSpeedUnit)

        binding.temp.text = String.format("%.1f°%s", currentTemp, getUnitSymbol(unit))
        binding.maxTemp.text = String.format("%.1f°%s", maxTemp, getUnitSymbol(unit))
        binding.miniTemp.text = String.format("%.1f°%s/", minTemp, getUnitSymbol(unit))
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = String.format(Locale.getDefault(), "%.1f %s", windSpeed, getWindSpeedUnitSymbol(windSpeedUnit))
        binding.sunrisee.text = time(weather.sys.sunrise.toLong())
        binding.sunset.text = time(weather.sys.sunset.toLong())
        binding.sea.text = "${weather.main.pressure}hpa"

        val weatherCondition = weather.weather.firstOrNull()
        binding.condition.text = weatherCondition?.main ?: "Unknown"

        binding.day.text = dayName()
        binding.date.text = date()
        binding.textLocation.text = city
        binding.time.text = time(System.currentTimeMillis() / 1000)


        if (isFirstTimeOpen()) {
            showUserGuide()
        }
        changeImageWeather(weatherCondition?.main ?: "unknown")

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
    }

    //**********************************************************************************************
    // Date and time.

    @SuppressLint("SimpleDateFormat")
    private fun date(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    private fun time(timesTemp: Long): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(Date(timesTemp * 1000))
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateCurrentTime(){
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        binding.time.text = currentTime
    }


    private fun dayName(): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    //**********************************************************************************************
    // location

    //**********************************************************************************************
    // Navigation

    fun notification(view: View) {
    }
    fun favourites(view: View) {}
    fun openLocation(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)
        startActivity(intent)
    }

    fun settings(view: View) {
        val settingsBottomSheet = SettingsBottomSheetDialog()
        settingsBottomSheet.show(supportFragmentManager, "SettingsBottomSheetDialog")
    }

    //**********************************************************************************************
    // Convert Temperature and Wind Speed

    private fun convertTemperature(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            "Celsius" -> tempInCelsius
            "Fahrenheit" -> (tempInCelsius * 9/5) + 32
            "Kelvin" -> tempInCelsius + 273.15
            else -> tempInCelsius
        }
    }

    fun convertWindSpeed(speed: Double, fromUnit: String, toUnit: String): Double {
        return when (toUnit) {
            "Miles/Hour" -> if (fromUnit == "Meter/Second") speed * 2.23694 else speed
            "Meter/Second" -> if (fromUnit == "Miles/Hour") speed / 2.23694 else speed
            else -> speed
        }
    }

    private fun getUnitSymbol(unit: String): String {
        return when (unit) {
            "Celsius" -> "C"
            "Fahrenheit" -> "F"
            "Kelvin" -> "K"
            else -> "C"
        }
    }

    private fun getWindSpeedUnitSymbol(unit: String): String {
        return when (unit) {
            "Meter/Second" -> "m/s"
            "Miles/Hour" -> "mph"
            else -> "m/s"
        }
    }

    //**********************************************************************************************
    // Language

    private fun setLocale(languageCode: String?) {
        if (languageCode != null) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    //**********************************************************************************************
    // Check Network
    override fun onResume() {
        super.onResume()
        networkChangeReceiver = NetworkChangeReceiver(this)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onNetworkChange(isConnected: Boolean) {
        if (isConnected){
            if (wasConnected){
                showCustomSnackbar("Back Online", Snackbar.LENGTH_SHORT)
                wasConnected = false
            }
        }else{
            showCustomSnackbar("Internet is not available", Snackbar.LENGTH_SHORT)
            wasConnected = true
        }
    }
    private fun checkNetworkStatus() {
        if (isConnectedToInternet(this)) {
            showCustomSnackbar("Connected to the Internet", Snackbar.LENGTH_SHORT)

        } else {
            showCustomSnackbar("No Internet connection", Snackbar.LENGTH_INDEFINITE)
        }
    }


    private fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = it.getNetworkCapabilities(it.activeNetwork)
                capabilities?.let { cap ->
                    return cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
            } else {
                val activeNetwork = it.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnected
            }
        }
        return false
    }

    @SuppressLint("RestrictedApi")
    private fun showCustomSnackbar(message: String, duration: Int) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "", duration)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customSnackbarView = inflater.inflate(R.layout.snackbar_custom, null)
        snackbarLayout.addView(customSnackbarView, 0)
        val textView = customSnackbarView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = message
        val iconView = customSnackbarView.findViewById<ImageView>(R.id.snackbar_icon)
        if (message.contains("Internet is not available")) {
            iconView.setImageResource(R.drawable.ic_no_internet)
        } else {
            iconView.setImageResource(R.drawable.ic_wifi)
        }
        snackbar.show()
    }

    //**********************************************************************************************
    // Tap Target

    @SuppressLint("ResourceType")
    private fun showUserGuide() {
        TapTargetView.showFor(this,
            TapTarget.forView(binding.favImage, "Add to favorites",
                "Save your favorite weather."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)

                    showNotificationGuide()
                }
            })
    }

    private fun showNotificationGuide() {
        TapTargetView.showFor(this,
            TapTarget.forView(binding.notificationImage, "Notifications",
                "View recent notifications."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)

                    showSettingsGuide()
                }
            })
    }

    private fun showSettingsGuide() {
        TapTargetView.showFor(this,
            TapTarget.forView(binding.settings, "Settings",
                "Convert temperature units, wind speed units, and change language."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showFavoritesGuide()
                }
            })
    }

    private fun showFavoritesGuide() {
        TapTargetView.showFor(this,
            TapTarget.forView(binding.openFav, "Open weather Favourites",
                "Show your saved favorite weather."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                }
            })
    }

    private fun isFirstTimeOpen(): Boolean {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean("isFirstTimeOpen", true)
        if (isFirstTime) {
            sharedPref.edit().putBoolean("isFirstTimeOpen", false).apply()
        }
        return isFirstTime
    }
    //**********************************************************************************************




}



