package com.example.cloud.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.AppDatabase
import com.example.cloud.database.CurrentWeatherEntity
import com.example.cloud.utils.PreferencesUtils
import com.example.cloud.utils.showUserGuide.showSwipeGuide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesBottomSheetDialog(
    private val appDatabase: AppDatabase,
    private val onCitySelected: (CurrentWeatherEntity) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteCitiesAdapter
    lateinit var titile: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_favourites, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        titile = view.findViewById(R.id.title)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            // Collect the weather data as Flow
            appDatabase.weatherDao().getAllWeatherData().collect { favoriteCitiesList ->
                withContext(Dispatchers.Main) {
                    adapter = FavoriteCitiesAdapter(favoriteCitiesList.toMutableList(), { weatherEntity ->
                        onCitySelected(weatherEntity)
                        dismiss()
                    }, { weatherEntity ->
                        // Delete city from database
                        CoroutineScope(Dispatchers.IO).launch {
                            appDatabase.weatherDao().deleteWeather(weatherEntity)
                            withContext(Dispatchers.Main) {
                                adapter.removeItem(weatherEntity)
                            }
                        }
                    })
                    recyclerView.adapter = adapter

                    // Attach item touch helper for swipe-to-delete functionality
                    val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallBack(adapter))
                    itemTouchHelper.attachToRecyclerView(recyclerView)

                    // Show swipe guide if not shown before
                    if (isAdded && isVisible && !PreferencesUtils.isGuideShown(requireContext())) {
                        showSwipeGuide(this@FavoritesBottomSheetDialog, titile)
                        PreferencesUtils.setGuideShown(requireContext(), true)
                    }
                }
            }
        }


    }


}


