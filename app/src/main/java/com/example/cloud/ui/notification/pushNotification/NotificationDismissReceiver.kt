package com.example.cloud.ui.notification.pushNotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.cloud.utils.PreferencesUtils

class NotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        decrementBadgeCount(context)
    }

    private fun decrementBadgeCount(context: Context) {
        val sharedPreferences: SharedPreferences =
            PreferencesUtils.getPreferences(context)
        val currentCount = sharedPreferences.getInt("notification_badge_count", 0)
        if (currentCount > 0) {
            sharedPreferences.edit().putInt("notification_badge_count", currentCount - 1).apply()

            val updateIntent = Intent("com.example.cloud.NOTIFICATION_COUNT_UPDATED")
            context.sendBroadcast(updateIntent)
        }
    }
}
