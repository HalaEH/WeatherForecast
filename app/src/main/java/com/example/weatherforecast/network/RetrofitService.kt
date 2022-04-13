package com.example.weatherforecast.network

import com.example.weatherforecast.model.OpenWeatherJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val appID = "de387e7ea9dc68b808121230c26aa3d7"

interface RetrofitService {
    @GET("onecall")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String =appID,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response<OpenWeatherJson>
}