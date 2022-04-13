package com.example.weatherforecast.ui.alerts.view

import com.example.weatherforecast.model.WeatherAlert

interface OnDeleteAlertClickListener {
    fun onClick(alert: WeatherAlert)
}