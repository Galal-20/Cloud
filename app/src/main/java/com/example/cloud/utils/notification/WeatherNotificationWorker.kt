package com.example.cloud.utils.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import com.example.cloud.repository.WeatherRepository
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
                val weatherRepository = WeatherRepository()
                val result = weatherRepository.getWeatherDataForNotification(lat, lon)
                result.fold(
                    onSuccess = { (currentWeather, hourlyForecast, dailyForecast) ->

                        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                        when (currentTime) {
                            "09:00" -> showNotification(
                                "Good Morning.",
                                "Current Temperature: ${currentWeather.main.temp}°C",
                                R.drawable.colud_background
                            )
                            "12:00" -> showNotification(
                                "Current Temperature.",
                                "Current Temperature: ${currentWeather.main.temp}°C",
                                R.drawable.colud_background
                            )
                            "15:00" -> showNotification(
                                "Current Temperature.",
                                "Temperature: ${currentWeather.main.temp}°C",
                                R.drawable.sunny_background
                            )
                            "19:00" -> showNotification(
                                "Forecast for Tomorrow.",
                                "Tomorrow's Forecast: Min ${dailyForecast.list[0].temp.min}°C / Max ${dailyForecast.list[0].temp.max}°C",
                                R.drawable.snow_background
                            )
                            else -> {
                                // Do nothing if it's not one of the target times
                                return@withContext Result.success()
                            }
                        }
                    },
                    onFailure = {
                        Result.failure()
                    }
                )
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

       /*
        val intent = Intent(context, Splash::class.java).apply {
            putExtra("notification_opened", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )*/


        // Intent to detect when the notification is dismissed
        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to decrement the badge count when the notification is opened
        val openNotificationIntent = Intent(context, NotificationDismissReceiver::class.java)
        val openPendingIntent = PendingIntent.getBroadcast(
            context, 0, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create Notification
        val notification = NotificationCompat.Builder(context, "WeatherChannel")
            .setSmallIcon(R.drawable.cloud_back) // Replace with your icon
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(context.getDrawable(imageRes)?.toBitmap())
            .setContentIntent(openPendingIntent)
            .setDeleteIntent(dismissPendingIntent) // Set delete intent.
            .setAutoCancel(true)
            .build()
        notificationManager.notify((0..1000).random(), notification) // Using random ID to allow multiple notifications
        incrementBadgeCount()
    }

    private fun incrementBadgeCount(){
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val currentCount = sharedPreferences.getInt("notification_badge_count", 0)
        sharedPreferences.edit().putInt("notification_badge_count", currentCount + 1).apply()

        // Send broadcast to notify MainActivity to update the badge count
        val intent = Intent("com.example.cloud.NOTIFICATION_COUNT_UPDATED")
        context.sendBroadcast(intent)
    }

}



/*
package com.example.cloud.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import com.example.cloud.repository.WeatherRepository
import com.example.cloud.R
import com.example.cloud.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val lat = inputData.getDouble("latitude", 15.5938)
        val lon = inputData.getDouble("longitude", 17.2663)

        return withContext(Dispatchers.IO) {
            try {
                // Fetch weather data
                val weatherRepository = WeatherRepository()
                val result = weatherRepository.getWeatherDataForNotification(lat, lon)
                result.fold(
                    onSuccess = { (currentWeather, hourlyForecast, dailyForecast) ->
                        // Show notification for morning weather update
                        showNotification(
                            "Morning Weather",
                            "Current Temperature: ${currentWeather.main.temp}°C",
                            R.drawable.sunny_background // Replace with actual icon
                        )
                        // Show notification for 10:10 PM hourly forecast
                        showNotification(
                            "10:10 PM Weather",
                            "Temperature: ${hourlyForecast.list[0].main.temp}°C",
                            R.drawable.sunny_background // Replace with actual icon
                        )

                        // Show notification for 7:00 PM daily forecast
                        showNotification(
                            "7:00 PM Weather",
                            "Tomorrow's Forecast: Min ${dailyForecast.list[0].temp.min}°C / Max " +
                                    "${dailyForecast.list[0].temp.max}°C",
                            R.drawable.sunny_background // Replace with actual icon
                        )
                    },
                    onFailure = {
                        Result.failure()
                    }
                )

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }

    private fun showNotification(title: String, content: String, imageRes: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "WeatherChannel",
                "Weather Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open the app when notification is clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create Notification
        val notification = NotificationCompat.Builder(context, "WeatherChannel")
            .setSmallIcon(R.drawable.cloud_back) // Replace with your icon
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(context.getDrawable(imageRes)?.toBitmap())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify((0..1000).random(), notification) // Using random ID to allow multiple notifications
    }
}
*/




/* private fun getSevenPmTime(): Long {
       // Proper logic to get 7 PM time in milliseconds
       val calendar = Calendar.getInstance().apply {
           set(Calendar.HOUR_OF_DAY, 19)
           set(Calendar.MINUTE, 0)
           set(Calendar.SECOND, 0)
       }
       return calendar.timeInMillis / 1000
   }*/
