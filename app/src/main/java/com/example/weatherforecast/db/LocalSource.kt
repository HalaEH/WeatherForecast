package com.example.weatherforecast.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    fun getContext(): Context
    fun insertWeather(openWeatherJason: OpenWeatherJson):Long
    suspend fun getCurrentWeatherLocal(id: Int): OpenWeatherJson
    fun getFavWeathers(): LiveData<List<OpenWeatherJson>>
    suspend fun getFavWeatherConditionLocal(lat: Double,lon:Double): OpenWeatherJson
    fun delete(favWeather: OpenWeatherJson)
    suspend fun insertWeatherAlert(weatherAlert: WeatherAlert)
    fun getWeatherAlerts(): LiveData<List<WeatherAlert>>
    fun deleteAlert(alert: WeatherAlert)




    fun getCurrentWeatherZone(lat: Double, long: Double): OpenWeatherJson
    suspend fun updateWeather(openWeatherJason: OpenWeatherJson)

}