package com.example.weatherforecast.ui.gallery.view

import com.example.weatherforecast.model.OpenWeatherJson

interface OnFavClickListener {
    fun onClick(favWeather: OpenWeatherJson)
}