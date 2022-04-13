package com.example.weatherforecast.ui.gallery.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel (iRepo: RepositoryInterface,context: Context) : ViewModel()  {
    private val _iRepo: RepositoryInterface = iRepo
    private val _context = context

    init {
        Log.i("TAG", "instance initializer: Creation of ViewModel")
        //  getCurrentWeather(lat,long)
    }
    //Expose returned online Data
    val onlineFavWeather: MutableLiveData<OpenWeatherJson> = MutableLiveData()
    lateinit var localFavWeather: LiveData<List<OpenWeatherJson>>

     fun getFavWeather(lat: Double, long: Double,units: String, lang: String){
        viewModelScope.launch(Dispatchers.IO){
            val weather = _iRepo.getCurrentWeather(lat,long,units,lang)
                Log.i("TAG", "getCurrentFavoriteWeather: ${weather}")
                onlineFavWeather.postValue(weather.body())
                insertLocalFav(weather.body()!!)
        }

    }

    fun localFavWeather(): LiveData<List<OpenWeatherJson>>{
        return _iRepo.getLocalFavWeathers()
    }

    fun insertLocalFav(fav: OpenWeatherJson){
        viewModelScope.launch(Dispatchers.IO){
            fav?.isFavourite = true
            _iRepo.insertWeather(fav)
        }
    }

    fun deleteFav(favWeather: OpenWeatherJson) {
        viewModelScope.launch(Dispatchers.IO) {
            _iRepo.deleteFav(favWeather)
        }
    }
}