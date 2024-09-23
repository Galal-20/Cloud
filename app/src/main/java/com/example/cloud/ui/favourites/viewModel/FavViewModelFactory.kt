package com.example.cloud.ui.favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloud.repository.local.Fav.WeatherFavRepositoryInterface

class FavViewModelFactory(private val repository: WeatherFavRepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}