package com.example.cloud.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtils {

    private const val PREFS_NAME = "AppPreferences"
    private const val KEY_GUIDE_SHOWN = "guide_shown"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
    private const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"

    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"



    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getLanguage(context: Context): String {
        return getPreferences(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, language: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getTemperatureUnit(context: Context): String {
        return getPreferences(context).getString(KEY_TEMPERATURE_UNIT, "Celsius") ?: "Celsius"
    }

    fun setTemperatureUnit(context: Context, unit: String) {
        getPreferences(context).edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    fun getWindSpeedUnit(context: Context): String {
        return getPreferences(context).getString(KEY_WIND_SPEED_UNIT, "Meter/Second") ?: "Meter/Second"
    }

    fun setWindSpeedUnit(context: Context, unit: String) {
        getPreferences(context).edit().putString(KEY_WIND_SPEED_UNIT, unit).apply()
    }


    fun isGuideShown(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_GUIDE_SHOWN, false)
    }

    fun setGuideShown(context: Context, shown: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_GUIDE_SHOWN, shown).apply()
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
}



