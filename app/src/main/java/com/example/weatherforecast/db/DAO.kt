package com.example.weatherforecast.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {

    //Insert into database(homescreen + favorites)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(openWeatherJason: OpenWeatherJson):Long

    //------------------------------------------Current Weather--------------------------------------------------------------
    //Get current weather (home screen)
    @Query("Select * from weather where  id =:id And isFavourite  =0 ")
    suspend fun getCurrentWeatherLocal(id: Int): OpenWeatherJson

    //------------------------------------------Favorites-------------------------------------------------------------------

    //get list of favourite Weathers
    @Query("Select * from weather where  isFavourite  =1 ")
    fun getFavWeathersZone(): LiveData<List<OpenWeatherJson>>

    //get conditions of favorite weather
    @Query("Select * from weather where  lat =:lat And lon =:lon and isFavourite  =1 ")
    suspend fun getFavWeatherConditionLocal(lat: Double,lon:Double): OpenWeatherJson

    //delete from favorites
    @Delete
    fun delete(favWeather: OpenWeatherJson)

    //------------------------------------------Alerts----------------------------------------------------------------------
    //insert alert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherAlert(alert: WeatherAlert)

    //get alert
    @Query("Select * from Alert ")
    fun getWeatherAlerts(): LiveData<List<WeatherAlert>>

    //delete alert
    @Delete
    fun deleteAlert(alert: WeatherAlert)


    @Update()
    suspend fun updateWeather(openWeatherJason: OpenWeatherJson)
    @Query("Select * from weather where  lat =:lat AND lon =:lon and isFavourite=0")
    fun getCurrentWeatherZone(lat: Double,lon: Double): OpenWeatherJson

}