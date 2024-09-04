package com.example.cloud.Ui.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cloud.R
import com.example.cloud.Ui.Main.MainActivity
import com.example.cloud.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch weather data
                val weatherRepository = WeatherRepository()
                //val result = weatherRepository.getWeatherDataForNotification("California") //
                // Replace
                // "London" with dynamic city input if needed

               /* result.fold(
                    onSuccess = { (currentWeather, hourlyForecast, dailyForecast) ->
                        // Show notification for morning weather update
                        showNotification(
                            "Morning Weather",
                            "Current Temperature: ${currentWeather.main.temp}°C",
                            R.drawable.sunny_background // Replace with actual icon
                        )

                        // Show notification for 7:00 PM hourly forecast
                        val sevenPmForecast = hourlyForecast.list.firstOrNull { it.dt == getSevenPmTime() }
                        if (sevenPmForecast != null) {
                            showNotification(
                                "7:00 PM Weather",
                                "Temperature: ${sevenPmForecast.main.temp}°C",
                                R.drawable.sunny_background // Replace with actual icon
                            )
                        }

                        // Show notification for 10:00 PM daily forecast
                        showNotification(
                            "10:00 PM Weather",
                            "Tomorrow's Forecast: Min ${dailyForecast.main.temp_min}°C / Max " +
                                    "${dailyForecast.main.temp_max}°C",
                            R.drawable.sunny_background // Replace with actual icon
                        )
                    },
                    onFailure = { error ->
                        Result.failure()
                    }
                )*/

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
            .setSmallIcon(R.drawable.sunny_background) // Replace with your icon
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(context.getDrawable(imageRes)?.toBitmap())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((0..1000).random(), notification) // Using random ID to allow multiple notifications
    }

    private fun getSevenPmTime(): Long {
        // Implement a function to calculate the timestamp for 7:00 PM in your timezone
        // This is just a placeholder function
        return System.currentTimeMillis() + (7 * 60 * 60 * 1000) // Placeholder logic for 7:00 PM
    }
}
