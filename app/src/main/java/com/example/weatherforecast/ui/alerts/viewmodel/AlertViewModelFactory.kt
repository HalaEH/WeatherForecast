package com.example.weatherforecast.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.RepositoryInterface
import java.lang.IllegalArgumentException

class AlertViewModelFactory(private val _irepo: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(_irepo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}