package com.example.cloud.ui.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.entity.AlarmEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmAdapter(
    private val onDeleteClicked: (AlarmEntity) -> Unit
) : ListAdapter<AlarmEntity, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
    }

    fun getItemAt(position: Int): AlarmEntity {
        return getItem(position)
    }

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(alarm: AlarmEntity) {
            itemView.findViewById<TextView>(R.id.date_time_alarm).text = formatTime(alarm.timeInMillis)
            // Other view bindings
        }

        private fun formatTime(timeInMillis: Long): String {
            // Format time as needed
            return SimpleDateFormat("hh:mm a, MMM dd yyyy", Locale.getDefault()).format(Date(timeInMillis))
            // Replace with actual formatting logic
        }
    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<AlarmEntity>() {
        override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
            return oldItem == newItem
        }
    }
}
