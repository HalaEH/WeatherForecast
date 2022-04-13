package com.example.weatherforecast.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.RepositoryInterface
import com.example.weatherforecast.ui.gallery.viewmodel.FavoriteViewModel
import java.lang.IllegalArgumentException

class HomeViewModelFactory  (private val _irepo: RepositoryInterface,private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_irepo,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}