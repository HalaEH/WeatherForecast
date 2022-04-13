package com.example.weatherforecast.ui.favoriteConditions.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.R
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.RepositoryInterface
import com.example.weatherforecast.utils.checkForInternet
import com.example.weatherforecast.utils.initSharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavConditionViewModel (iRepo: RepositoryInterface, context: Context) : ViewModel()  {
    private val _iRepo: RepositoryInterface = iRepo
    private val _context = context
    lateinit var sharedPreferences: SharedPreferences

    init {
        Log.i("TAG", "instance initializer: Creation of ViewModel")
        //  getCurrentWeather(lat,long)
    }
    //Expose returned online Data
    val onlineWeather: MutableLiveData<OpenWeatherJson> = MutableLiveData()

    fun getCurrentWeather(lat: Double, long: Double,units: String, lang: String){
        viewModelScope.launch(Dispatchers.IO){
                val weather = _iRepo.getFavWeather(lat,long,units,lang)
                Log.i("TAG", "getCurrentWeather: ${weather}")
                onlineWeather.postValue(weather.body())
        }
    }
}