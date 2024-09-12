package com.example.cloud.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cloud.R
import com.example.cloud.model.ListElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DaysAdapter : ListAdapter<ListElement, DaysAdapter.DayViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dailyData = getItem(position)
        holder.bind(dailyData)
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.day_text_view)
        private val weatherImageView: ImageView = itemView.findViewById(R.id.day_weather_image_view)
        private val weatherDescriptionTextView: TextView = itemView.findViewById(R.id.day_weather_description)
        private val maxDegreeTextView: TextView = itemView.findViewById(R.id.day_max_degree)
        private val minDegreeTextView: TextView = itemView.findViewById(R.id.day_min_degree)

        fun bind(dailyData: ListElement) {
            val unit = itemView.context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getString("temperature_unit", "Celsius") ?: "Celsius"

            val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val date = Date(dailyData.dt * 1000)
            dayTextView.text = dateFormat.format(date)

            val weatherCondition = dailyData.weather.firstOrNull()
            weatherDescriptionTextView.text = weatherCondition?.description ?: "Unknown"

            val iconUrl = "https://openweathermap.org/img/wn/${weatherCondition?.icon}@2x.png"
            Glide.with(itemView.context)
                .load(iconUrl)
                .placeholder(R.drawable.baseline_cloud_queue_24)
                .error(R.drawable.wind)
                .into(weatherImageView)

            val maxTemp = convertTemperature(dailyData.temp.max, unit)
            val minTemp = convertTemperature(dailyData.temp.min, unit)


            maxDegreeTextView.text = String.format("%.1f°%s", maxTemp, getUnitSymbol(unit))
            minDegreeTextView.text = String.format("%.1f°%s", minTemp, getUnitSymbol(unit))
        }

        fun convertTemperature(tempCelsius: Double, unit: String): Double {
            return when (unit) {
                "Fahrenheit" -> tempCelsius * 9/5 + 32
                "Kelvin" -> tempCelsius + 273.15
                else -> tempCelsius
            }
        }
        private fun getUnitSymbol(unit: String): String {
            return when (unit) {
                "Fahrenheit" -> "F"
                "Kelvin" -> "K"
                else -> "C"
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<ListElement>() {
        override fun areItemsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
            return oldItem == newItem
        }
    }
}








