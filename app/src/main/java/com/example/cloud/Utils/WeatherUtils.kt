package com.example.cloud.Utils


class WeatherUtils {
    companion object {
        fun kelvinToCelsius(kelvin: Double): String {
            val result = kelvin - 273.15
            return result.toInt().toString()
        }

        fun kelvinToFahrenheit(kelvin: Double): String {
            val celsius = kelvin - 273.15
            val result = (celsius * 9 / 5) + 32
            return result.toInt().toString()
        }

        fun meterPerSecondToMilesPerHour(mps: Double): Int {
            return (mps * 2.23694).toInt()
        }
    }
}
