package com.example.weatherforecast.ui.alerts.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.RepositoryInterface
import com.example.weatherforecast.model.WeatherAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlertViewModel (iRepo: RepositoryInterface) : ViewModel()  {
    private val _iRepo: RepositoryInterface = iRepo


    fun getWeatherAlerts(): LiveData<List<WeatherAlert>> {
        return _iRepo.getWeatherAlerts()
    }

    fun insertAlert(alert: WeatherAlert){
        viewModelScope.launch(Dispatchers.IO){
            _iRepo.insertWeatherAlert(alert)
        }
    }

    fun deleteAlert(alert: WeatherAlert){
        viewModelScope.launch(Dispatchers.IO){
            _iRepo.deleteAlert(alert)
        }
    }

}