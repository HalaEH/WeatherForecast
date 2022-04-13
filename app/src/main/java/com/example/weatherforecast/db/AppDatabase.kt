package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.model.Converters
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.WeatherAlert

@Database(entities = [OpenWeatherJson::class, WeatherAlert::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun weatherDAO(): DAO?

    companion object{
        private var instance: AppDatabase? = null
        //one thread at a time to access this method
        @Synchronized
        open fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "weather")
                        .build()
            }
            return instance as AppDatabase
        }
    }

}