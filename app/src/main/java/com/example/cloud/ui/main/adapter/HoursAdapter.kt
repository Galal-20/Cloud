package com.example.cloud.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cloud.R
import com.example.cloud.databinding.HoursItemBinding
import com.example.cloud.model.HourlyListElement
import com.example.cloud.utils.Settings.convertTemperature
import com.example.cloud.utils.Settings.getUnitSymbol
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HoursAdapter : ListAdapter<HourlyListElement, HoursAdapter.HourlyWeatherViewHolder>
    (HourlyWeatherDiffCallback()) {

    inner class HourlyWeatherViewHolder(private val binding: HoursItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DefaultLocale")
        fun bind(hourlyWeather: HourlyListElement) {
            val unit = binding.root.context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getString("temperature_unit", R.string.celsius.toString()) ?: R.string.celsius.toString()

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_in_animation)
            itemView.startAnimation(animation)

            binding.timeTextView.text = formatTime(hourlyWeather.dt)

            val temp = convertTemperature(hourlyWeather.main.temp, unit)
            binding.hoursDegree.text = String.format("%.0f°%s", temp, getUnitSymbol(unit))

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





