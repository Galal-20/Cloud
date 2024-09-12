package com.example.cloud.ui.favourites


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.CurrentWeatherEntity

class FavoriteCitiesAdapter(
    private val favoriteCities: MutableList<CurrentWeatherEntity>,
    private val onCityClicked: (CurrentWeatherEntity) -> Unit,
    val onDeleteClicked: (CurrentWeatherEntity) -> Unit
) : RecyclerView.Adapter<FavoriteCitiesAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)
        private val deleteItem: ImageView = itemView.findViewById(R.id.delete_item)

        fun bind(weatherEntity: CurrentWeatherEntity) {
            cityName.text = weatherEntity.city
            itemView.setOnClickListener {
                onCityClicked(weatherEntity)
            }
            deleteItem.setOnClickListener {
                onDeleteClicked(weatherEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(favoriteCities[position])
    }

    override fun getItemCount(): Int = favoriteCities.size

    fun removeItem(weatherEntity: CurrentWeatherEntity) {
        favoriteCities.remove(weatherEntity)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CurrentWeatherEntity {
        return favoriteCities[position]
    }
}




