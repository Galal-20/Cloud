package com.example.cloud.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object NotificationPermission {

    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Toast.makeText(activity, "Notification permission already granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to handle permission result
    fun handlePermissionResult(
        activity: Activity,
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
