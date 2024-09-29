package com.example.cloud.ui.notification.pushNotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

   override suspend fun doWork(): Result {
       val lat = inputData.getDouble("latitude", 0.0)
       val lon = inputData.getDouble("longitude", 0.0)

       return withContext(Dispatchers.IO) {
           try {
               val weatherRepository = WeatherRepositoryImpl(context)

               weatherRepository.getWeatherDataForNotification(lat, lon).collect { weatherData ->
                   val (currentWeather, hourlyForecast, dailyForecast) = weatherData

                   val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                   when (currentTime) {
                       "05:00" ->showNotification (
                               "Good Morning.",
                       "Current Temperature: ${currentWeather.main.temp}°C",
                       R.drawable.colud_background
                           )
                           "12:00"
                       -> showNotification(
                           "Current Temperature.",
                           "Current Temperature: ${hourlyForecast.list[0].main.temp}°C",
                           R.drawable.sunny_background
                       )
                       "15:00" -> showNotification(
                           "Current Temperature.",
                           "Temperature: ${hourlyForecast.list[0].main.temp}°C",
                           R.drawable.sunny_background
                       )

                       "20:15" -> showNotification(
                           "Forecast for Tomorrow.",
                           "Tomorrow's Forecast: Min ${dailyForecast.list[0].temp.min}°C / Max ${dailyForecast.list[0].temp.max}°C",
                           R.drawable.snow_background
                       )

                       else -> {
                           return@collect
                       }
                   }
               }

               Result.success()

           } catch (e: Exception) {
               Result.failure()
           }
       }
   }




    private fun showNotification(title: String, content: String, imageRes: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "WeatherChannel",
                "Weather Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }



        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openNotificationIntent = Intent(context, NotificationDismissReceiver::class.java)
        val openPendingIntent = PendingIntent.getBroadcast(
            context, 0, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "WeatherChannel")
            .setSmallIcon(R.drawable.cloud_back)
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(context.getDrawable(imageRes)?.toBitmap())
            .setContentIntent(openPendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify((0..1000).random(), notification)
        incrementBadgeCount()
    }

    private fun incrementBadgeCount(){
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val currentCount = sharedPreferences.getInt("notification_badge_count", 0)
        sharedPreferences.edit().putInt("notification_badge_count", currentCount + 1).apply()

        val intent = Intent("com.example.cloud.NOTIFICATION_COUNT_UPDATED")
        context.sendBroadcast(intent)
    }

}


