package com.example.weatherforecast.ui.alerts.view

interface AlertCommunicator {
    fun alarm(fromDate: Long, toDate: Long, time: String)
}