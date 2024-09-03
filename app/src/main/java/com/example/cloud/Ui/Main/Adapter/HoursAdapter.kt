package com.example.cloud.Ui.Main.Adapter

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
            binding.timeTextView.text = formatTime(hourlyWeather.dt)
            binding.hoursDegree.text = "${hourlyWeather.main.temp}°C"

            val iconCode = hourlyWeather.weather.firstOrNull()?.icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

            Glide.with(binding.hoursWeatherIcon.context)
                .load(iconUrl)
                .into(binding.hoursWeatherIcon)
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





