package com.example.weatherforecast.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforecast.R
import com.example.weatherforecast.db.LocalSource
import com.example.weatherforecast.network.RemoteSource
import com.example.weatherforecast.utils.initSharedPref
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class Repository private constructor(
    var remoteSource: RemoteSource,
    var localSource: LocalSource,
    var context: Context
): RepositoryInterface {

    companion object{
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource,localSource: LocalSource,
                        context: Context): Repository{
            return instance?: Repository(
                remoteSource,localSource, context)
        }
    }

    override suspend fun getCurrentWeather(lat: Double, long: Double,units: String, lang: String): Response<OpenWeatherJson> {
        val response = remoteSource.getCurrentWeather(lat, long,units,lang)
            val sharedPreferences = initSharedPref(context)
            // check if already exist or not
            val idResult = sharedPreferences.getInt(
                context.getString(R.string.ID), -3
            )
            // At Beginning
            if (idResult != -3) {
                //means no id for home yet
                response.body()?.id = idResult
            }
            val id = localSource.insertWeather(response.body()!!).toInt()
            sharedPreferences.edit().apply {
                putInt(
                    context.getString(R.string.ID),
                    id
                )
                putFloat(
                    context.getString(R.string.LAT),
                    response.body()!!.lat!!.toFloat()
                )
                putFloat(
                    context.getString(R.string.LON),
                    response.body()!!.lon!!.toFloat()
                )
                apply()
            }

       return remoteSource.getCurrentWeather(lat,long,units,lang)
    }

    override suspend fun getFavWeather(lat: Double, long: Double,units: String, lang: String): Response<OpenWeatherJson> {
        return remoteSource.getCurrentWeather(lat,long,units,lang)
    }

    override suspend fun getCurrentWeatherLocal(id:Int): OpenWeatherJson {
        return localSource.getCurrentWeatherLocal(id)
    }

    override suspend fun getFavWeatherConditionLocal(lat: Double, lon: Double): OpenWeatherJson {
        return localSource.getFavWeatherConditionLocal(lat,lon)
    }

    override fun getLocalFavWeathers(): LiveData<List<OpenWeatherJson>> {
        return localSource.getFavWeathers()
    }

    override fun insertWeather(favWeather: OpenWeatherJson) {
       localSource.insertWeather(favWeather)
    }

    override fun deleteFav(favWeather: OpenWeatherJson) {
        localSource.delete(favWeather)
    }

    override suspend fun insertWeatherAlert(weatherAlert: WeatherAlert) {
         localSource.insertWeatherAlert(weatherAlert)
    }

    override fun deleteAlert(alert: WeatherAlert) {
        localSource.deleteAlert(alert)
    }

    override fun getWeatherAlerts(): LiveData<List<WeatherAlert>> {
        return localSource.getWeatherAlerts()
    }


}
