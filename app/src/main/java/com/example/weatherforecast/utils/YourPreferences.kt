package com.example.weatherforecast.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Languages
import com.example.weatherforecast.model.Units

class YourPreferences {
    private var sharedPreferences: SharedPreferences? = null

    companion object{
        private var yourPreference: YourPreferences? = null
        fun getInstance(context: Context?): YourPreferences {
            if (yourPreference == null) {
               yourPreference = YourPreferences(context)
            }
            return yourPreference as YourPreferences
        }
    }

    constructor(context: Context?) {
        sharedPreferences = context!!.getSharedPreferences("YourCustomNamedPreference", Context.MODE_PRIVATE)
    }

    fun saveData(key: String?, value: String?) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun getData(key: String?): String? {
        return if (sharedPreferences != null) {
            sharedPreferences!!.getString(key, "")
        } else ""
    }
}

fun initSharedPref(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.shared_pref),
        Context.MODE_PRIVATE
    )
}

fun initFavSharedPref(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.shared_fav_pref),
        Context.MODE_PRIVATE
    )
}

//Setting the temp units
fun initUNIT(unit: String, context: Context) {
    initSharedPref(context).edit().apply {
        putString(context.getString(R.string.UNITS), unit)
        apply()
    }
}

//get temp units (C- K - f)
fun getTempUnit(context: Context): String {
    return when (getUnitSystem(context)) {
        Units.METRIC.name -> {
            "C"
        }
        Units.IMPERIAL.name -> {
            "F"
        }
        Units.STANDARD.name -> {
            "K"
        }
        else -> {
            "C"
        }
    }
}

//get temp unit system (standard(kelvin) - metric - imperial)
fun getUnitSystem(context: Context): String {
    return initSharedPref(context).getString(
        context.getString(R.string.UNITS),
        Units.METRIC.name
    ) ?: Units.METRIC.name
}

//get wind speed units (meter/s - mile/hour)
fun getWindUnit(context: Context): String{
    return when (getUnitSystem(context)){
        Units.METRIC.name->{
            "meter/sec"
        }
        Units.IMPERIAL.name->{
            "miles/hour"
        }
        else->{
            "miles/hour"
        }
    }
}

//Setting the temp units
fun initLANG(lang: String, context: Context) {
    initSharedPref(context).edit().apply {
        //false-> EN
        putString(context.getString(R.string.LANG), lang)
        apply()
    }
}


fun getLang(context: Context): String{
    return initSharedPref(context).getString(
        context.getString(R.string.LANG),
        Languages.ENGLISH.name
    )?:Languages.ENGLISH.name
}

fun checkForInternet(context: Context): Boolean {

    // register activity with the connectivity manager service
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // if the android version is equal to M
    // or greater we need to use the
    // NetworkCapabilities to check what type of
    // network has the internet connection
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    } else {
        // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}
