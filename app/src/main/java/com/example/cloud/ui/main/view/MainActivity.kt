package com.example.cloud.ui.main.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud.R
import com.example.cloud.ui.main.adapter.DaysAdapter
import com.example.cloud.ui.main.adapter.HoursAdapter
import com.example.cloud.ui.map.MapActivity
import com.example.cloud.ui.settings.SettingsBottomSheetDialog
import com.example.cloud.utils.network.NetworkChangeReceiver
import com.example.cloud.utils.showUserGuide
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.model.Daily
import com.example.cloud.model.Hourly
import com.example.cloud.model.HourlyListElement
import com.example.cloud.model.ListElement
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import com.example.cloud.retrofit.ApiState
import com.example.cloud.ui.favourites.view.FavoritesBottomSheetDialog
import com.example.cloud.ui.favourites.viewModel.FavViewModel
import com.example.cloud.ui.favourites.viewModel.FavViewModelFactory
import com.example.cloud.ui.notification.alarm.AlertBottomSheetDialog
import com.example.cloud.utils.network.Check_Network
import com.example.cloud.ui.notification.pushNotification.NotificationPermission
import com.example.cloud.ui.notification.pushNotification.NotificationScheduler.scheduleWeatherNotifications
import com.example.cloud.utils.PreferencesUtils
import com.example.cloud.utils.Settings.convertTemperature
import com.example.cloud.utils.Settings.convertWindSpeed
import com.example.cloud.utils.Settings.date
import com.example.cloud.utils.Settings.dayName
import com.example.cloud.utils.Settings.getUnitSymbol
import com.example.cloud.utils.Settings.getWindSpeedUnitSymbol
import com.example.cloud.utils.Settings.setLocale
import com.example.cloud.utils.Settings.time
import com.example.cloud.utils.Settings.updateCurrentTime
import com.example.cloud.utils.room.WeatherUtils.saveWeatherToDatabase
import com.galal.weather.ViewModel.WeatherViewModel
import com.galal.weather.ViewModel.WeatherViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {

    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val weatherViewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(WeatherRepositoryImpl()) }
    private val favViewModel: FavViewModel by viewModels {
        FavViewModelFactory(WeatherFavRepositoryImp(AppDatabase.getDatabase(this).weatherDao()))
    }

    private val appDatabase by lazy { AppDatabase.getDatabase(this) }
    private val handle = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime(binding.time)
            binding.day.text = dayName()
            handle.postDelayed(this, 1000)
        }
    }
    private val updateAnimation = object : Runnable {
        override fun run() {
            binding.lottieAnimationView.playAnimation()
            handle.postDelayed(this, 2000)
        }
    }
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private lateinit var checkNetwork: Check_Network
    private val badgeCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotificationBadge()
        }
    }
    private lateinit var sharedPreferences: SharedPreferences
    private var city: String =""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var isFavorites = false
    private lateinit var pascalUnit:String
    private var pressureValue :Int = 0

    private var currentData: Daily? = null
    private var hourlyData: List<HourlyListElement> = emptyList()
    private var dailyForecastList: List<ListElement> = emptyList()



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        sharedPreferences = PreferencesUtils.getPreferences(this)
        val language = PreferencesUtils.getLanguage(this)
        setLocale(this, language)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(badgeCountReceiver, IntentFilter("com.example.cloud.NOTIFICATION_COUNT_UPDATED"),
                RECEIVER_EXPORTED
            )
        }

        checkNetwork = Check_Network(this, findViewById(android.R.id.content))
        networkChangeReceiver = NetworkChangeReceiver(this)
        onClick()
        NotificationPermission.requestNotificationPermission(this)
        lat = intent.getDoubleExtra("latitude", 0.0)
        lon = intent.getDoubleExtra("longitude", 0.0)
        setupObservers()

        updateNotificationBadge()
        handle.post(updateTimeRunnable)
        handle.post(updateAnimation)
        binding.date.text = date()
        updateFavouriteIcon()
        scheduleWeatherNotifications(this, lat, lon)

    }

    // lifecycle
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkChangeReceiver?.let {
            registerReceiver(it, filter)
        }
        updateNotificationBadge()
    }
    override fun onPause() {
        super.onPause()
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
        updateNotificationBadge()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(badgeCountReceiver)
    }
    //**********************************************************************************************
    // Check Network
    override fun onNetworkChange(isConnected: Boolean) {
        checkNetwork.onNetworkChange(isConnected)
        if (isConnected) {
            fetchData()
        } else {
            Log.d("MainActivity", "Internet is not available. Showing offline data.")
            //loadOfflineData()
            lifecycleScope.launch {
                loadWeatherDataFromSharedPreferences()
            }
        }
    }
    //**********************************************************************************************
    // onClick
    private fun onClick(){
        binding.favImage.setOnClickListener { addToFavourites() }
        binding.textLocation.setOnClickListener { openLocation() }
        binding.settings.setOnClickListener { settings() }
        binding.openFav.setOnClickListener { openFav() }
        binding.notificationImage.setOnClickListener { openNotification() }
    }
    //**********************************************************************************************
    // Observer & fetchData
    private fun setupObservers() {
        lifecycleScope.launch {
            weatherViewModel.weatherDataStateFlow.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (apiState.data is Daily) {
                            currentData = apiState.data

                            // Fetch and check the hourly forecast data
                            when (val hourlyState = weatherViewModel.hourlyForecastDataByCoordinates.value) {
                                is ApiState.Success -> {
                                    if (hourlyState.data is Hourly) {
                                        hourlyData = hourlyState.data.list // Assuming list is the correct property
                                    } else {
                                        Log.e("WeatherError", "Expected Hourly data but received: ${hourlyState.data::class.simpleName}")
                                        hourlyData = emptyList()
                                    }                                }
                                is ApiState.Loading -> {
                                    hourlyData = emptyList()
                                }
                                is ApiState.Failure -> {
                                    Log.e("WeatherError", "Error retrieving hourly forecast: ${hourlyState.message}")
                                    hourlyData = emptyList()
                                }
                            }

                            // Fetch and check the daily forecast data
                            when (val dailyState = weatherViewModel.dailyForecastDataByCoordinates.value) {
                                is ApiState.Success -> {
                                    if (dailyState.data is Daily) {
                                        dailyForecastList = dailyState.data.list
                                    }else{
                                        dailyForecastList = emptyList()
                                    }

                                }
                                is ApiState.Loading -> {
                                    dailyForecastList = emptyList()
                                }
                                is ApiState.Failure -> {
                                    Log.e("WeatherError", "Error retrieving daily forecast: ${dailyState.message}")
                                    dailyForecastList = emptyList()
                                }
                            }

                            // Update the UI with retrieved data
                            updateUI(currentData!!)
                            setupHourlyForecastRecyclerView(hourlyData)
                            setupDailyForecastRecyclerView(dailyForecastList)
                            lifecycleScope.launch {
                                saveWeatherDataToSharedPreferences(
                                    currentData!!,
                                    hourlyData,
                                    dailyForecastList
                                )
                            }

                        } else {
                            Log.e("WeatherError", "Expected Daily data but received: ${apiState.data::class.simpleName}")
                        }

                    }

                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e(
                            "WeatherError",
                            "Error retrieving daily forecast data ${apiState.message}"
                        )
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            weatherViewModel.hourlyForecastDataByCoordinates.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val hourlyForecast = apiState.data
                        if (hourlyForecast is Hourly) {
                            val hourlyForecastList =
                                hourlyForecast.list
                            setupHourlyForecastRecyclerView(hourlyForecastList)
                        } else {
                            Log.e(
                                "WeatherError",
                                "Unexpected data type: ${hourlyForecast.javaClass}"
                            )
                        }
                    }

                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e(
                            "WeatherError",
                            "Error retrieving hourly forecast data: ${apiState.message}"
                        )
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            weatherViewModel.dailyForecastDataByCoordinates.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> {
                       binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val dailyForecast = apiState.data
                        if (dailyForecast is Daily) {
                            setupDailyForecastRecyclerView(dailyForecast.list)
                        } else {
                            Log.e(
                                "WeatherError",
                                "Unexpected data type: ${dailyForecast.javaClass}"
                            )
                        }
                    }

                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e(
                            "WeatherError",
                            "Error retrieving hourly forecast data: ${apiState.message}"
                        )
                    }
                }
            }
        }
    }


    private fun fetchData(){
        if (checkNetwork.isConnectedToInternet(this)) {
            if (lat != 0.0 && lon != 0.0) {
                lifecycleScope.launch {
                    weatherViewModel.fetchCurrentWeatherDataByCoordinates(lat, lon)
                    weatherViewModel.fetchHourlyWeatherByCoordinates(lat, lon)
                    weatherViewModel.fetchDailyWeatherByCoordinates(lat, lon)
                }
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        city = "${addresses[0].adminArea}, ${addresses[0].countryName}"
                        binding.textLocation.text = city
                    } else {
                        city = R.string.Unknown.toString()
                        binding.textLocation.text = R.string.Location_Unknown.toString()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, R.string.Unable, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("MainActivity", "Invalid coordinates: lat=$lat, lon=$lon")
            }
        } else {
            Toast.makeText(this, R.string.Not_Internet, Toast.LENGTH_SHORT).show()
            city = R.string.Unknown.toString()
        }
    }

    //**********************************************************************************************
    // update Ui.
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateUI(weather: Daily) {
        val unit = PreferencesUtils.getTemperatureUnit(this)
        val windSpeedUnit = PreferencesUtils.getWindSpeedUnit(this)
        val currentTemp = convertTemperature(weather.main.temp, unit)
        val maxTemp = convertTemperature(weather.main.temp_max, unit)
        val minTemp = convertTemperature(weather.main.temp_min, unit)
        val windSpeed = convertWindSpeed(weather.wind.speed, R.string.meter_second.toString(), windSpeedUnit)
        binding.temp.text = String.format("%.0f°%s", currentTemp, getUnitSymbol(unit))
        binding.maxTemp.text = String.format("%.0f°%s", maxTemp, getUnitSymbol(unit))
        binding.miniTemp.text = String.format("%.0f°%s/", minTemp, getUnitSymbol(unit))
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = String.format(Locale.getDefault(),
            "%.0f %s", windSpeed, getWindSpeedUnitSymbol(windSpeedUnit))
        binding.sunrisee.text = time(weather.sys.sunrise.toLong())
        binding.sunset.text = time(weather.sys.sunset.toLong())
        pascalUnit = binding.root.context.getString(R.string.pascal)
        pressureValue = weather.main.pressure
        binding.sea.text = "$pressureValue $pascalUnit"
        val weatherCondition = weather.weather.firstOrNull()
        binding.today.text = weatherCondition?.main ?: R.string.Unknown.toString()
        binding.condition.text = weather.clouds.all.toString() + getString(R.string.percentage)
        binding.day.text = dayName()
        binding.date.text = date()
        binding.textLocation.text = city
        binding.time.text = time(System.currentTimeMillis() / 1000)
        if(PreferencesUtils.isGuideShown(this).not()){showUserGuide.showUserGuide(this, binding)}
        changeImageWeather(weatherCondition?.main ?: R.string.Unknown.toString())
    }

    private fun changeImageWeather(conditions: String) {
        when (conditions) {
            "Clouds", "Mist", "Foggy", "Overcast", "Partly Clouds","Snow" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear", "Sunny", "Clear Sky" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Heavy Rain", "Rain","Showers", "Moderate Rain", "Drizzle", "Light Rain" -> {
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

    private fun updateNotificationBadge() {
        val notificationCount = sharedPreferences.getInt("notification_badge_count", 0)
        val badgeTextView = findViewById<TextView>(R.id.notification_badge)
        badgeTextView.text = notificationCount.toString()
        badgeTextView.visibility = if (notificationCount > 0) View.VISIBLE else View.GONE
    }

    //**********************************************************************************************
    // Navigation

    private fun openNotification() {
        val alertBottomSheetDialog = AlertBottomSheetDialog()
        alertBottomSheetDialog.show(supportFragmentManager, "AlertBottomSheetDialog")
    }

    private fun addToFavourites() {
        isFavorites = !isFavorites
        if (isFavorites) {
            binding.favImage.setImageResource(R.drawable.added_to_favorite)
            saveWeatherToDatabase(this, weatherViewModel, lat, lon, city)
        }
    }

    private fun updateFavouriteIcon() = isFavorites.takeIf { it }?.let { binding.favImage.setImageResource(R.drawable.added_to_favorite) }

    private fun openLocation() {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)
        startActivity(intent)
    }

    private fun settings() = SettingsBottomSheetDialog().show(supportFragmentManager, "SettingsBottomSheetDialog")

    private fun openFav() = FavoritesBottomSheetDialog(appDatabase,::showWeatherData).show(supportFragmentManager, "FavoritesBottomSheetDialog")


    //**********************************************************************************************
    // Database:
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showWeatherData(weatherEntity: CurrentWeatherEntity) {
        binding.temp.text = String.format("%.0f°%s", weatherEntity.temperature, getUnitSymbol(R.string.celsius.toString()))
        binding.maxTemp.text = String.format("%.0f°%s", weatherEntity.temperatureMax, getUnitSymbol(R.string.celsius.toString()))
        binding.miniTemp.text = String.format("%.0f°%s/", weatherEntity.temperatureMin, getUnitSymbol(R.string.celsius.toString()))
        binding.humidity.text = "${weatherEntity.humidity} %"
        binding.windSpeed.text = String.format(Locale.getDefault(),
            "%.0f %s", weatherEntity.windSpeed,
            getWindSpeedUnitSymbol(R.string.meter_second.toString()))
        binding.sunrisee.text = time(weatherEntity.sunrise)
        binding.sunset.text = time(weatherEntity.sunset)
        pascalUnit = binding.root.context.getString(R.string.pascal)
        pressureValue = weatherEntity.seaPressure
        binding.sea.text = "$pressureValue $pascalUnit"
        binding.today.text = weatherEntity.main
        binding.condition.text = weatherEntity.clouds.toString() + getString(R.string.percentage)
        changeImageWeatherS(weatherEntity.imageWeather)
        binding.textLocation.text = weatherEntity.city
        binding.date.text = date()
        binding.time.text = time(System.currentTimeMillis() / 1000)
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
    private fun loadOfflineData() {
        CoroutineScope(Dispatchers.IO).launch {
            val weatherEntity = favViewModel.getFirstWeatherItem()
            withContext(Dispatchers.Main) {
                if (weatherEntity != null) {
                    binding.progressBar.visibility = View.GONE
                    showWeatherData(weatherEntity)
                } else {
                    Toast.makeText(this@MainActivity, R.string.no_offline_data_available.toString(), Toast
                        .LENGTH_SHORT).show()
                }
            }
        }
    }




    //**********************************************************************************************
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        NotificationPermission.handlePermissionResult(this, requestCode, grantResults)
    }

    // *********************************************************************************************
    // Save weather data to SharedPreferences
  /*  private fun saveWeatherDataToSharedPreferences(currentWeather: Daily, hourlyWeather: List<HourlyListElement>, dailyWeather:List<ListElement>) {

        if (currentWeather == null) return

        val gson = Gson()
        val editor = sharedPreferences.edit()

        // Save current weather as JSON string
        val currentWeatherJson = gson.toJson(currentWeather)
        editor.putString("currentWeather", currentWeatherJson)

        // Save hourly weather as JSON string
        val hourlyWeatherJson = gson.toJson(hourlyWeather)
        editor.putString("hourlyWeather", hourlyWeatherJson)

        // Save daily weather as JSON string
        val dailyWeatherJson = gson.toJson(dailyWeather)
        editor.putString("dailyWeather", dailyWeatherJson)

        editor.apply() // Apply changes
        Toast.makeText(this, "Data saved successfully in shared preferences", Toast.LENGTH_SHORT).show()
    }

    // Load weather data from SharedPreferences when offline
    private fun loadWeatherDataFromSharedPreferences() {
        val gson = Gson()

        // Retrieve current weather
        val currentWeatherJson = sharedPreferences.getString("currentWeather", null)
        val currentWeather = if (currentWeatherJson != null) {
            gson.fromJson(currentWeatherJson, Daily::class.java)
        } else {
            null
        }
        Log.d("local", "Current Weather: $currentWeather")

        // Retrieve hourly weather
        val hourlyWeatherJson = sharedPreferences.getString("hourlyWeather", null)
        val hourlyWeatherList = if (hourlyWeatherJson != null) {
            val type = object : TypeToken<List<HourlyListElement>>() {}.type
            gson.fromJson<List<HourlyListElement>>(hourlyWeatherJson, type)
        } else {
            null
        }
        Log.d("local", "Current Weather: $hourlyWeatherList")


        // Retrieve daily weather
        val dailyWeatherJson = sharedPreferences.getString("dailyWeather", null)
        val dailyWeatherList = if (dailyWeatherJson != null) {
            val type = object : TypeToken<List<ListElement>>() {}.type
            gson.fromJson<List<ListElement>>(dailyWeatherJson, type)
        } else {
            null
        }
        Log.d("local", "Current Weather: $dailyWeatherList")


        // Update UI if data is available
        if (currentWeather != null &&  dailyWeatherList != null) {
            updateUI(currentWeather)
            //setupHourlyForecastRecyclerView(hourlyWeatherList)
            setupDailyForecastRecyclerView(dailyWeatherList)
            binding.progressBar.visibility = View.GONE
            binding.textLocation.visibility = View.GONE
        } else {
            Toast.makeText(this, getString(R.string.no_offline_data_available), Toast.LENGTH_SHORT).show()
        }
    }*/


    // Save weather data to SharedPreferences
    private suspend fun saveWeatherDataToSharedPreferences(currentWeather: Daily, hourlyWeather: List<HourlyListElement>, dailyWeather: List<ListElement>) {
        // Ensure currentWeather is not null
        if (currentWeather == null) return

        withContext(Dispatchers.IO) { // Switch to IO context for saving data
            val gson = Gson()
            val editor = sharedPreferences.edit()

            // Save current weather as JSON string
            val currentWeatherJson = gson.toJson(currentWeather)
            editor.putString("currentWeather", currentWeatherJson)

            // Save hourly weather as JSON string
            val hourlyWeatherJson = gson.toJson(hourlyWeather)
            editor.putString("hourlyWeather", hourlyWeatherJson)

            // Save daily weather as JSON string
            val dailyWeatherJson = gson.toJson(dailyWeather)
            editor.putString("dailyWeather", dailyWeatherJson)

            editor.apply() // Apply changes

          /*  Log.d("save", "Current Weather: $currentWeatherJson")
            Log.d("save", "Hourly Weather: $hourlyWeatherJson")
            Log.d("save", "Daily Weather: $dailyWeatherJson")*/
        }

        // Show toast in Main thread after saving
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, "Data saved successfully in shared preferences", Toast.LENGTH_SHORT).show()

        }
    }

    // Load weather data from SharedPreferences when offline
    private suspend fun loadWeatherDataFromSharedPreferences() {
        withContext(Dispatchers.IO) { // Switch to IO context for loading data
            val gson = Gson()

            // Retrieve current weather
            val currentWeatherJson = sharedPreferences.getString("currentWeather", null)
            val currentWeather = currentWeatherJson?.let {
                gson.fromJson(it, Daily::class.java)
            }

            // Retrieve hourly weather
            val hourlyWeatherJson = sharedPreferences.getString("hourlyWeather", null)
            val hourlyWeatherList = hourlyWeatherJson?.let {
                val type = object : TypeToken<List<HourlyListElement>>() {}.type
                gson.fromJson<List<HourlyListElement>>(it, type)
            }

            // Retrieve daily weather
            val dailyWeatherJson = sharedPreferences.getString("dailyWeather", null)
            val dailyWeatherList = dailyWeatherJson?.let {
                val type = object : TypeToken<List<ListElement>>() {}.type
                gson.fromJson<List<ListElement>>(it, type)
            }

            // Update UI on the main thread
            withContext(Dispatchers.Main) {
                Log.d("local", "Current Weather: $currentWeather")
                Log.d("local", "Hourly Weather: $hourlyWeatherList")
                Log.d("local", "Daily Weather: $dailyWeatherList")

                // Update UI if data is available
                if (currentWeather != null && dailyWeatherList != null) {
                    updateUI(currentWeather)
                    if (hourlyWeatherList != null) {
                        setupHourlyForecastRecyclerView(hourlyWeatherList)
                    } // Uncomment if needed
                    setupDailyForecastRecyclerView(dailyWeatherList)
                    binding.progressBar.visibility = View.GONE
                    binding.textLocation.visibility = View.GONE
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.no_offline_data_available), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}