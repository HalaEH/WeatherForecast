package com.example.weatherforecast.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource: LocalSource {

    var dao: DAO? = null
    constructor(context: Context){
        val database: AppDatabase = AppDatabase.getInstance(context)!!
        dao = database.weatherDAO()!!
    }

    companion object{
        var localSource: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource{
            if(localSource == null)
                localSource = ConcreteLocalSource(context)
            return localSource as ConcreteLocalSource
        }
    }

    override  fun insertWeather(openWeatherJason: OpenWeatherJson):Long {
        return dao!!.insertWeather(openWeatherJason)
    }

    override fun getCurrentWeatherZone(lat: Double, long: Double): OpenWeatherJson {
        return dao!!.getCurrentWeatherZone(lat,long)
    }

    override suspend fun getCurrentWeatherLocal(id: Int): OpenWeatherJson {
        return dao!!.getCurrentWeatherLocal(id)
    }

    override suspend fun updateWeather(openWeatherJason: OpenWeatherJson) {
        TODO("Not yet implemented")
    }

    override fun getContext(): Context {
        return getContext()
    }

    override fun getFavWeathers(): LiveData<List<OpenWeatherJson>> {
        return dao!!.getFavWeathersZone()
    }

    override suspend fun getFavWeatherConditionLocal(lat: Double, lon: Double): OpenWeatherJson {
        return dao!!.getFavWeatherConditionLocal(lat, lon)
    }

    override fun delete(favWeather: OpenWeatherJson) {
        return dao!!.delete(favWeather)
    }

    override suspend fun insertWeatherAlert(weatherAlert: WeatherAlert){
         dao!!.insertWeatherAlert(weatherAlert)
    }

    override  fun getWeatherAlerts(): LiveData<List<WeatherAlert>> {
        return dao!!.getWeatherAlerts()
    }

    override fun deleteAlert(alert: WeatherAlert) {
        return dao!!.deleteAlert(alert)
    }


}