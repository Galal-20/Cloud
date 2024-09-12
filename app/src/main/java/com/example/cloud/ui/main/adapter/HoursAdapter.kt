package com.example.cloud.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cloud.databinding.HoursItemBinding
import com.example.cloud.model.HourlyListElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HoursAdapter : ListAdapter<HourlyListElement, HoursAdapter.HourlyWeatherViewHolder>
    (HourlyWeatherDiffCallback()) {

    inner class HourlyWeatherViewHolder(private val binding: HoursItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hourlyWeather: HourlyListElement) {
            val unit = binding.root.context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getString("temperature_unit", "Celsius") ?: "Celsius"

            binding.timeTextView.text = formatTime(hourlyWeather.dt)

            val temp = convertTemperature(hourlyWeather.main.temp, unit)
            binding.hoursDegree.text = String.format("%.1f°%s", temp, getUnitSymbol(unit))

            val iconCode = hourlyWeather.weather.firstOrNull()?.icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

            Glide.with(binding.hoursWeatherIcon.context)
                .load(iconUrl)
                .into(binding.hoursWeatherIcon)
        }

        private fun getUnitSymbol(unit: String): String {
            return when (unit) {
                "Fahrenheit" -> "F"
                "Kelvin" -> "K"
                else -> "C"
            }
        }
        fun convertTemperature(tempCelsius: Double, unit: String): Double {
            return when (unit) {
                "Fahrenheit" -> tempCelsius * 9/5 + 32
                "Kelvin" -> tempCelsius + 273.15
                else -> tempCelsius
            }
        }

        private fun formatTime(timestamp: Long): String {
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp * 1000))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding = HoursItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyListElement>() {
        override fun areItemsTheSame(oldItem: HourlyListElement, newItem: HourlyListElement): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: HourlyListElement, newItem: HourlyListElement): Boolean {
            return oldItem == newItem
        }
    }
}





