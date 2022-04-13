package com.example.weatherforecast.network

import com.example.weatherforecast.model.OpenWeatherJson
import retrofit2.Response


private const val appID = "de387e7ea9dc68b808121230c26aa3d7"
class WeatherClient private constructor(): RemoteSource{
    override suspend fun getCurrentWeather(lat: Double, long: Double,units: String, lang: String): Response<OpenWeatherJson> {
        val weatherService = RetrofitHelper.getInstance().create(RetrofitService::class.java)
        val response = weatherService.getCurrentWeather(lat,long, appID,units,lang)
        return response
    }

    companion object{
        private var instance:WeatherClient? = null
        fun getInstance():WeatherClient{
            return instance?:WeatherClient()
        }
    }


}