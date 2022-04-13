package com.example.weatherforecast.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>): String {
        return Gson().toJson(hourly)
    }

    @TypeConverter
    fun fromDailyToString(daily: List<Daily>): String {
        return Gson().toJson(daily)
    }

    @TypeConverter
    fun fromCurrentToString(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun fromStringToCurrent(current: String): Current {
        return Gson().fromJson(current, Current::class.java)
    }

    @TypeConverter
    fun fromAlertsToString(alerts: List<Alerts>?): String {
        if (!alerts.isNullOrEmpty()) {
            return Gson().toJson(alerts)
        }
        return ""
    }


    @TypeConverter
    fun fromStringToHourly(hourly: String): List<Hourly> {
        val listType: Type = object : TypeToken<List<Hourly?>?>() {}.type
        return Gson().fromJson(hourly, listType)
    }

    @TypeConverter
    fun fromStringToDaily(daily: String): List<Daily> {
        val listType = object : TypeToken<List<Daily?>?>() {}.type
        return Gson().fromJson(daily, listType)
    }

    @TypeConverter
    fun fromStringToAlerts(alerts: String?): List<Alerts> {
        if (alerts.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<Alerts?>?>() {}.type
        return Gson().fromJson(alerts, listType)
    }


    @TypeConverter
    fun fromWeatherToString(weather: Weather): String {
        return Gson().toJson(weather)
    }

    @TypeConverter
    fun fromStringToWeather(weather: String): Weather {
        val listType = object : TypeToken<List<Weather?>?>() {}.type
        return Gson().fromJson(weather, listType)
    }

}