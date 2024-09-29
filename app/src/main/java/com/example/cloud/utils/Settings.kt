package com.example.cloud.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
object Settings{

    fun convertTemperature(tempInCelsius: Double, unit: String): Double {
        return when (unit) {
            "Celsius" -> tempInCelsius
            "Fahrenheit" -> (tempInCelsius * 9/5) + 32
            "Kelvin" -> tempInCelsius + 273.15
            else -> tempInCelsius
        }
    }

    fun convertWindSpeed(speed: Double, fromUnit: String, toUnit: String): Double {
        return when (toUnit) {
            "Miles/Hour" -> if (fromUnit == "Meter/Second") speed * 2.23694 else speed
            "Meter/Second" -> if (fromUnit == "Miles/Hour") speed / 2.23694 else speed
            else -> speed
        }
    }

     fun getUnitSymbol(unit: String): String {
        return when (unit) {
            "Celsius" -> "C"
            "Fahrenheit" -> "F"
            "Kelvin" -> "K"
            else -> "C"
        }
    }

     fun getWindSpeedUnitSymbol(unit: String): String {
        return when (unit) {
            "Meter/Second" -> "m/s"
            "Miles/Hour" -> "mph"
            else -> "m/s"
        }
    }

    fun setLocale(context: Context, languageCode: String?) {
        if (languageCode != null) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun date(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    fun time(timesTemp: Long): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(Date(timesTemp * 1000))
    }

    @SuppressLint("SimpleDateFormat")
    fun updateCurrentTime(binding: TextView) {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        binding.text = currentTime
    }

    fun dayName(): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }


     fun formatTime(timeInMillis: Long): String {
        return SimpleDateFormat("hh:mm a, MMM dd yyyy", Locale.getDefault()).format(Date(timeInMillis))
    }




}