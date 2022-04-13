package com.example.weatherforecast.network

import com.example.weatherforecast.model.OpenWeatherJson
import retrofit2.Response

interface RemoteSource {
    suspend fun getCurrentWeather(
        lat: Double,
        long: Double,
        units: String,
        lang: String
    ): Response<OpenWeatherJson>
}