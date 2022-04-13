package com.example.weatherforecast.model

import androidx.lifecycle.LiveData
import retrofit2.Response


interface RepositoryInterface {
    //from network
    suspend fun getCurrentWeather(lat: Double, long: Double,units: String, lang: String): Response<OpenWeatherJson>
    suspend fun getFavWeather(lat: Double,long: Double,units: String, lang: String): Response<OpenWeatherJson>
    fun getWeatherAlerts(): LiveData<List<WeatherAlert>>
    suspend fun insertWeatherAlert(weatherAlert: WeatherAlert)
    fun deleteAlert(alert: WeatherAlert)


    //local-room
    suspend fun getCurrentWeatherLocal(id:Int): OpenWeatherJson
    suspend fun getFavWeatherConditionLocal(lat: Double,lon:Double): OpenWeatherJson
    fun getLocalFavWeathers(): LiveData<List<OpenWeatherJson>>
    fun insertWeather(favWeather: OpenWeatherJson)
    fun deleteFav(favWeather: OpenWeatherJson)


}