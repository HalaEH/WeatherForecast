package com.example.weatherforecast.ui.home.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.R
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.RepositoryInterface
import com.example.weatherforecast.utils.YourPreferences
import com.example.weatherforecast.utils.checkForInternet
import com.example.weatherforecast.utils.initSharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.AccessController.getContext

class HomeViewModel (iRepo: RepositoryInterface,contex: Context) : ViewModel()  {
        private val _iRepo: RepositoryInterface = iRepo
        private val _context = contex
        lateinit var sharedPreferences:SharedPreferences

        init {
            Log.i("TAG", "instance initializer: Creation of ViewModel")
            //  getCurrentWeather(lat,long)
        }
        //Expose returned online Data
        val onlineWeather: MutableLiveData<OpenWeatherJson> = MutableLiveData()
        fun getCurrentWeather(lat: Double, long: Double,units: String, lang: String){
                viewModelScope.launch(Dispatchers.IO){
                    if (checkForInternet(_context)){
                        val weather = _iRepo.getCurrentWeather(lat, long,units,lang)
                        Log.i("TAG", "getCurrentWeather: ${weather}")
                        onlineWeather.postValue(weather.body())
                     //   insertLocal(weather.body()!!)
                    }else{
                        val sharedPreferences = initSharedPref(_context)
                        val id = sharedPreferences.getInt(
                            _context.getString(R.string.ID), -3
                        )
                        onlineWeather.postValue(_iRepo.getCurrentWeatherLocal(id))
                        Log.d("HOMEEEEEE", _iRepo.getCurrentWeatherLocal(id).toString())
                    }
                }
        }
}