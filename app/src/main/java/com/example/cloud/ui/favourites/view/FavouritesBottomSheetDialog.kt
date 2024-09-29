package com.example.cloud.ui.favourites.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.entity.CurrentWeatherEntity
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryImp
import com.example.cloud.ui.favourites.adapter.FavoriteCitiesAdapter
import com.example.cloud.ui.favourites.viewModel.FavViewModel
import com.example.cloud.ui.favourites.viewModel.FavViewModelFactory
import com.example.cloud.ui.offline.OfflineActivity
import com.example.cloud.utils.PreferencesUtils
import com.example.cloud.utils.showUserGuide.showSwipeGuide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class FavoritesBottomSheetDialog(
    private val appDatabase: AppDatabase,
    private val onCityDeleted: () -> Unit

) : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteCitiesAdapter
    lateinit var title: TextView

    private val repository by lazy { WeatherFavRepositoryImp(appDatabase.weatherDao()) }

    private val viewModel: FavViewModel by viewModels {
        FavViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_favourites, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        title = view.findViewById(R.id.title)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.allWeatherData.collect { favoriteCitiesList ->
                adapter = FavoriteCitiesAdapter(favoriteCitiesList.toMutableList(), { weatherEntity ->
                    openOfflineActivity(weatherEntity)
                    dismiss()
                }, { weatherEntity ->
                    showDeleteConfirmationDialog(weatherEntity)

                })
                recyclerView.adapter = adapter

                val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallBack(adapter))
                itemTouchHelper.attachToRecyclerView(recyclerView)

                if (isAdded && isVisible && !PreferencesUtils.isGuideShown(requireContext())) {
                    showSwipeGuide(this@FavoritesBottomSheetDialog, title)
                    PreferencesUtils.setGuideShown(requireContext(), true)
                }
            }
        }
    }
    private fun openOfflineActivity(weatherEntity: CurrentWeatherEntity) {
        val intent = Intent(requireContext(), OfflineActivity::class.java).apply {
            putExtra("temperature", weatherEntity.temperature)
            putExtra("temperatureMax", weatherEntity.temperatureMax)
            putExtra("temperatureMin", weatherEntity.temperatureMin)
            putExtra("humidity", weatherEntity.humidity)
            putExtra("windSpeed", weatherEntity.windSpeed)
            putExtra("seaPressure", weatherEntity.seaPressure)
            putExtra("sunrise", weatherEntity.sunrise)
            putExtra("sunset", weatherEntity.sunset)
            putExtra("description", weatherEntity.description)
            putExtra("clouds", weatherEntity.clouds)
            putExtra("imageWeather", weatherEntity.imageWeather)
            putExtra("city", weatherEntity.city)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(weatherEntity: CurrentWeatherEntity) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    viewModel.deleteWeather(weatherEntity)
                    adapter.removeItem(weatherEntity)
                    onCityDeleted()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

