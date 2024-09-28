package com.example.cloud.ui.notification.pushNotification

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleWeatherNotifications(context: Context, lat: Double, lon: Double) {
        val workManager = WorkManager.getInstance(context)

        val inputData = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", lon)
            .build()

        val morningRequest = createWorkRequest("08:53", inputData)
        workManager.enqueueUniqueWork(
            "MorningWeatherNotification",
            ExistingWorkPolicy.REPLACE,
            morningRequest
        )

        val morningRequest2 = createWorkRequest("12:00", inputData)
        workManager.enqueueUniqueWork(
            "MorningWeatherNotification2",
            ExistingWorkPolicy.REPLACE,
            morningRequest2
        )

        val afternoonRequest = createWorkRequest("15:00", inputData)
        workManager.enqueueUniqueWork(
            "AfternoonWeatherNotification",
            ExistingWorkPolicy.REPLACE,
            afternoonRequest
        )

        val eveningRequest = createWorkRequest("20:35", inputData)
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
}


