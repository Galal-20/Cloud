package com.example.cloud.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cloud.R
import com.example.cloud.model.ListElement
import com.example.cloud.utils.PreferencesUtils
import com.example.cloud.utils.Settings.convertTemperature
import com.example.cloud.utils.Settings.getUnitSymbol
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DaysAdapter : ListAdapter<ListElement, DaysAdapter.DayViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val adjustedPosition = position + 1
        val dailyData = getItem(adjustedPosition)
        holder.bind(dailyData)
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.day_text_view)
        private val weatherImageView: ImageView = itemView.findViewById(R.id.day_weather_image_view)
        private val weatherDescriptionTextView: TextView = itemView.findViewById(R.id.day_weather_description)
        private val maxDegreeTextView: TextView = itemView.findViewById(R.id.day_max_degree)
        private val minDegreeTextView: TextView = itemView.findViewById(R.id.day_min_degree)

        @SuppressLint("DefaultLocale")
        fun bind(dailyData: ListElement) {

            val unit = PreferencesUtils.getPreferences(itemView.context).getString("temperature_unit", R.string.celsius.toString()) ?: R.string.celsius.toString()
            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_in_animation)
            itemView.startAnimation(animation)

            val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val date = Date(dailyData.dt * 1000)
            dayTextView.text = dateFormat.format(date)

            val weatherCondition = dailyData.weather.firstOrNull()
            weatherDescriptionTextView.text = weatherCondition?.main ?: R.string.Unknown.toString()

            val iconUrl = "https://openweathermap.org/img/wn/${weatherCondition?.icon}@2x.png"
            Glide.with(itemView.context)
                .load(iconUrl)
                .placeholder(R.drawable.baseline_cloud_queue_24)
                .error(R.drawable.wind)
                .into(weatherImageView)

            val maxTemp = convertTemperature(dailyData.temp.max, unit)
            val minTemp = convertTemperature(dailyData.temp.min, unit)


            maxDegreeTextView.text = String.format("%.0f°%s", maxTemp, getUnitSymbol(unit))
            minDegreeTextView.text = String.format("%.0f°%s", minTemp, getUnitSymbol(unit))
        }

    }

    override fun getItemCount(): Int {
        return super.getItemCount() - 1
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








