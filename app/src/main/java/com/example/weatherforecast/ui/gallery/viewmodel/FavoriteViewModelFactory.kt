package com.example.weatherforecast.ui.gallery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.RepositoryInterface
import java.lang.IllegalArgumentException

class FavoriteViewModelFactory (private val _irepo: RepositoryInterface, private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(_irepo,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}