package com.example.cloud.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cloud.R
import com.example.cloud.ui.main.adapter.DaysAdapter
import com.example.cloud.ui.main.adapter.HoursAdapter
import com.example.cloud.ui.map.MapActivity
import com.example.cloud.ui.settings.SettingsBottomSheetDialog
import com.example.cloud.utils.NetworkChangeReceiver
import com.example.cloud.utils.Settings
import com.example.cloud.utils.showUserGuide
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.CurrentWeatherEntity
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.model.Daily
import com.example.cloud.model.HourlyListElement
import com.example.cloud.model.ListElement
import com.example.cloud.repository.WeatherRepository
import com.example.cloud.ui.favourites.FavoritesBottomSheetDialog
import com.example.cloud.ui.notification.WeatherNotificationWorker
import com.example.cloud.utils.Check_Network
import com.example.cloud.utils.NotificationPermission
import com.example.cloud.utils.PreferencesUtils
import com.galal.weather.ViewModel.WeatherViewModel
import com.galal.weather.ViewModel.WeatherViewModelFactory
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private var city: String =""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var isFavorites = false
    private val weatherViewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(WeatherRepository()) }
    private val appDatabase by lazy { AppDatabase.getDatabase(this) }
    private val handle = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            settings.updateCurrentTime(binding.time)
            binding.day.text = settings.dayName()
            handle.postDelayed(this, 1000)
        }
    }
    private val updateAnimation = object : Runnable {
        override fun run() {
            binding.lottieAnimationView.playAnimation()
            handle.postDelayed(this, 2000)
        }
    }
    val settings = Settings()
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private lateinit var checkNetwork: Check_Network

    private var notificationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkNetwork = Check_Network(this, findViewById(android.R.id.content))
        networkChangeReceiver = NetworkChangeReceiver(this)
        onClick()
        NotificationPermission.requestNotificationPermission(this)
        lat = intent.getDoubleExtra("latitude", 0.0)
        lon = intent.getDoubleExtra("longitude", 0.0)
        setupObservers()
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en")
        settings.setLocale(this,language)
        handle.post(updateTimeRunnable)
        handle.post(updateAnimation)
        binding.date.text = settings.date()
        fetchData()
        updateFavouriteIcon()
        scheduleWeatherNotifications()

    }

    // Check Network
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
    override fun onNetworkChange(isConnected: Boolean) = checkNetwork.onNetworkChange(isConnected)
    //**********************************************************************************************
    // onClick
    private fun onClick(){
        binding.favImage.setOnClickListener { addToFavourites() }
        binding.textLocation.setOnClickListener { openLocation() }
        binding.settings.setOnClickListener { settings() }
        binding.openFav.setOnClickListener { openFav() }
        binding.notificationImage.setOnClickListener {
            notificationCount++
            updateNotificationBadge()

        }
    }
    private fun updateNotificationBadge() {
        val badgeTextView = findViewById<TextView>(R.id.notification_badge)
        badgeTextView.text = notificationCount.toString()
        badgeTextView.visibility = if (notificationCount > 0) View.VISIBLE else View.GONE
    }
    //**********************************************************************************************

    // Observer & fetchData
    private fun setupObservers() {

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

    private fun fetchData(){
        if (checkNetwork.isConnectedToInternet(this)) {
            if (lat != 0.0 && lon != 0.0) {
                weatherViewModel.fetchWeatherByCoordinates(lat, lon)
                weatherViewModel.fetchHourlyWeatherByCoordinates(lat, lon)
                weatherViewModel.fetchDailyWeatherByCoordinates(lat, lon)

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
                Toast.makeText(this, R.string.Invalid, Toast.LENGTH_SHORT).show()
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
        val unit = sharedPreferences.getString("temperature_unit", "Celsius") ?: "Celsius"
        val windSpeedUnit = sharedPreferences.getString("wind_speed_unit", "Meter/Second") ?: "Meter/Second"

        val currentTemp = settings.convertTemperature(weather.main.temp, unit)
        val maxTemp = settings.convertTemperature(weather.main.temp_max, unit)
        val minTemp = settings.convertTemperature(weather.main.temp_min, unit)
        val windSpeed = settings.convertWindSpeed(weather.wind.speed, "Meter/Second", windSpeedUnit)
        binding.temp.text = String.format("%.1f°%s", currentTemp, settings.getUnitSymbol(unit))
        binding.maxTemp.text = String.format("%.1f°%s", maxTemp, settings.getUnitSymbol(unit))
        binding.miniTemp.text = String.format("%.1f°%s/", minTemp, settings.getUnitSymbol(unit))
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = String.format(Locale.getDefault(),
            "%.1f %s", windSpeed, settings.getWindSpeedUnitSymbol(windSpeedUnit))
        binding.sunrisee.text = settings.time(weather.sys.sunrise.toLong())
        binding.sunset.text = settings.time(weather.sys.sunset.toLong())
        binding.sea.text = "${weather.main.pressure}hpa"
        val weatherCondition = weather.weather.firstOrNull()
        binding.condition.text = weatherCondition?.main ?: "Unknown"
        binding.day.text = settings.dayName()
        binding.date.text = settings.date()
        binding.textLocation.text = city
        binding.time.text = settings.time(System.currentTimeMillis() / 1000)
        if(PreferencesUtils.isGuideShown(this).not()){showUserGuide.showUserGuide(this, binding)}
        changeImageWeather(weatherCondition?.main ?: "unknown")
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
    // Navigation

    private fun addToFavourites() {
        isFavorites = !isFavorites
        if (isFavorites) {
            binding.favImage.setImageResource(R.drawable.added_to_favorite)
            saveWeatherToDatabase()
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
    private fun saveWeatherToDatabase() {
        weatherViewModel.weatherDataByCoordinates.value?.let { result ->
            result.fold(
                onSuccess = { weather ->
                    val weatherCondition = weather.weather.firstOrNull()?.main ?: "Unknown"
                    val imageWeather = getImageWeatherForCondition(weatherCondition)
                    val weatherEntity = CurrentWeatherEntity(
                        lat = lat,
                        lon = lon,
                        city = city,
                        day = settings.dayName(),
                        temperature = weather.main.temp,
                        main = weatherCondition,
                        icon = weather.weather.firstOrNull()?.icon ?: "",
                        date = System.currentTimeMillis(),
                        temperatureMin = weather.main.temp_min,
                        temperatureMax = weather.main.temp_max,
                        timestamp = System.currentTimeMillis(),
                        windSpeed = weather.wind.speed,
                        seaPressure = weather.main.pressure,
                        sunset = weather.sys.sunset.toLong(),
                        sunrise = weather.sys.sunrise.toLong(),
                        humidity = weather.main.humidity,
                        lottieAnimation = getLottieAnimationForCondition(weatherCondition),
                        imageWeather = imageWeather
                    )

                    lifecycleScope.launch {
                        try {
                            appDatabase.weatherDao().insertWeather(weatherEntity)
                            //Log.d("MainActivity", "Weather data saved successfully")
                            Toast.makeText(this@MainActivity, "Weather data saved to favorites", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            //Log.e("MainActivity", "Error saving weather data", e)
                            Toast.makeText(this@MainActivity, "Failed to save weather data", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onFailure = {
                    Toast.makeText(this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showWeatherData(weatherEntity: CurrentWeatherEntity) {
        binding.temp.text = String.format("%.1f°%s", weatherEntity.temperature, settings.getUnitSymbol("Celsius"))
        binding.maxTemp.text = String.format("%.1f°%s", weatherEntity.temperatureMax, settings.getUnitSymbol("Celsius"))
        binding.miniTemp.text = String.format("%.1f°%s/", weatherEntity.temperatureMin, settings.getUnitSymbol("Celsius"))
        binding.humidity.text = "${weatherEntity.humidity} %"
        binding.windSpeed.text = String.format(Locale.getDefault(),
            "%.1f %s", weatherEntity.windSpeed, settings.getWindSpeedUnitSymbol("Meter/Second"))
        binding.sunrisee.text = settings.time(weatherEntity.sunrise)
        binding.sunset.text = settings.time(weatherEntity.sunset)
        binding.sea.text = "${weatherEntity.seaPressure} hpa"
        binding.condition.text = weatherEntity.main
        changeImageWeatherS(weatherEntity.imageWeather)
        binding.textLocation.text = weatherEntity.city
        binding.date.text = settings.date()
        binding.time.text = settings.time(System.currentTimeMillis() / 1000)
        binding.day.text = settings.dayName()

    }

    private fun getImageWeatherForCondition(condition: String): String {
        return when (condition) {
            "Clouds", "Mist", "Foggy", "Overcast", "Partly Clouds" ,"Snow"-> "cloud_background"
            "Clear", "Sunny", "Clear Sky" -> "sunny_background"
            "Heavy Rain", "Showers", "Moderate Rain", "Drizzle", "Light Rain" -> "rain_background"
            "Heavy Snow", "Moderate Snow", "Blizzard", "Light Snow" -> "snow_background"
            else -> "sunny_background"
        }
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

    private fun getLottieAnimationForCondition(condition: String): String {
        return when (condition) {
            "Clouds" -> "cloud_animation.json"
            "Clear" -> "sun_animation.json"
            "Rain" -> "rain_animation.json"
            "Snow" -> "snow_animation.json"
            else -> "default_animation.json"
        }
    }
    //**********************************************************************************************

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        NotificationPermission.handlePermissionResult(this, requestCode, grantResults)
    }


   private fun scheduleWeatherNotifications() {
       val workManager = WorkManager.getInstance(this)

       val inputData = Data.Builder()
           .putDouble("latitude", lat)
           .putDouble("longitude", lon)
           .build()

       val morningRequest = createWorkRequest("05:00", inputData)
       workManager.enqueueUniqueWork(
           "MorningWeatherNotification",
           ExistingWorkPolicy.REPLACE,
           morningRequest
       )

       val afternoonRequest = createWorkRequest("12:00", inputData)
       workManager.enqueueUniqueWork(
           "AfternoonWeatherNotification",
           ExistingWorkPolicy.REPLACE,
           afternoonRequest
       )

       val eveningRequest = createWorkRequest("19:00", inputData)
       workManager.enqueueUniqueWork(
           "EveningWeatherNotification",
           ExistingWorkPolicy.REPLACE,
           eveningRequest
       )
   }


    private fun createWorkRequest(time: String, inputData: Data): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        return OneTimeWorkRequestBuilder<WeatherNotificationWorker>()
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(time), TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
    }

    private fun calculateInitialDelay(time: String): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance()

        val (hour, minute) = time.split(":").map { it.toInt() }
        targetTime.set(Calendar.HOUR_OF_DAY, hour)
        targetTime.set(Calendar.MINUTE, minute)
        targetTime.set(Calendar.SECOND, 0)
        targetTime.set(Calendar.MILLISECOND, 0)

        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        return targetTime.timeInMillis - currentTime.timeInMillis
    }


    //**********************************************************************************************

}









