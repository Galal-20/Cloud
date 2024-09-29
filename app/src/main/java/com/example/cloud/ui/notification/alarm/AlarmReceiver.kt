package com.example.cloud.ui.notification.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.cloud.R
import com.example.cloud.model.Daily
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.retrofit.ApiState
import com.galal.weather.ViewModel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AlarmReceiver : BroadcastReceiver(), CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private lateinit var weatherViewModel: WeatherViewModel
    private var content: String? = null

    @SuppressLint("LaunchActivityFromNotification")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val alarmIntent = Intent(it, AlarmService::class.java)
            it.startService(alarmIntent)

            weatherViewModel = WeatherViewModel(WeatherRepositoryImpl(it))
            val lat = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
            val lon = intent?.getDoubleExtra("lon", 0.0) ?: 0.0

            launch {
                fetchWeather(it, lat, lon)
            }
        }
    }

    private suspend fun fetchWeather(context: Context, lat: Double, lon: Double) {
        weatherViewModel.fetchCurrentWeatherDataByCoordinates(lat, lon)
        weatherViewModel.weatherDataStateFlow.collect { apiState ->
            when (apiState) {
                is ApiState.Success -> {
                    val currentTemp = apiState.data
                    updateUi(currentTemp as Daily)
                    createNotification(context)
                }
                is ApiState.Failure -> {}
                is ApiState.Loading -> {}
            }
        }
    }

    private fun updateUi(weatherData: Daily) {
        content = "The weather today is ${weatherData.weather.firstOrNull()?.description}"
    }

    private fun createNotification(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "ALARM_CHANNEL"
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, DismissAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Weather Alert!")
            .setContentText(content ?: "No weather data available")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.ic_no_internet, "Dismiss", pendingIntent)
            .setSound(null)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    fun finalize() {
        job.cancel()
    }
}


