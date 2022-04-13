package com.example.weatherforecast.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.RepositoryInterface
import com.example.weatherforecast.model.WeatherAlert
import com.example.weatherforecast.network.WeatherClient
import com.example.weatherforecast.utils.Constants
import com.example.weatherforecast.utils.YourPreferences
import com.example.weatherforecast.utils.getLang
import com.example.weatherforecast.utils.getUnitSystem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class MyWorkManager(var context: Context, workerParams: WorkerParameters) : Worker(
    context, workerParams
) {
    lateinit var preference: YourPreferences
    lateinit var _irepo: Repository


    var alerts: ArrayList<WeatherAlert>? = null
    var position = 0
    private fun parseJSON(string: String?) {
        val gson = Gson()
        val type = object : TypeToken<List<WeatherAlert?>?>() {}.type
        alerts = gson.fromJson(string, type)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val data = inputData
        val now = LocalDateTime.now()
        if (data.getInt("time_nw", 0) == now.minute + now.hour) return Result.success()

        parseJSON(data.getString(Constants.ALERT_LIST))
        position = data.getInt(Constants.ALERT_POSITION, 0)

        fetchData()
        setAlarm(alerts)
        return Result.success()
    }

    fun showNotification(context: Context, title: String?, message: String?, reqCode: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val CHANNEL_ID = "10" // The id of the channel.
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alert_white)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Channel Name" // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(
            reqCode,
            notificationBuilder.build()
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setAlarm(alerts: ArrayList<WeatherAlert>?) {
        WorkManager.getInstance().cancelAllWorkByTag("alarm")
        if (alerts != null && !alerts.isEmpty()) {
            val gson = Gson()
            val medListString = gson.toJson(alerts)
            val timeNow = LocalDateTime.now()

            var timeAt = LocalDate.now().atTime(
                Integer.valueOf(alerts[0].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                Integer.valueOf(
                    alerts[0].time.split(":").toTypedArray()[1].trim { it <= ' ' })
            ) //hours minutes
            var duration = Duration.between(timeNow, timeAt)
            var position = 0
            for (i in alerts.indices) {
                timeAt = LocalDate.now().atTime(
                    Integer.valueOf(alerts[i].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                    Integer.valueOf(
                        alerts[i].time.split(":").toTypedArray()[1].trim { it <= ' ' })
                )
                //check today between start date and end date
                if (Calendar.getInstance().time.time >= alerts[i].startDay!! && Calendar.getInstance().time.time <= alerts[i].endDay!!) {
                    if (timeAt.isAfter(timeNow) && duration.abs().toMillis() > Duration.between(
                            timeNow,
                            timeAt
                        ).toMillis()
                    ) {
                        duration = Duration.between(timeNow, timeAt)
                        position = i
                    }
                }
            }
            val data = Data.Builder()
                .putString(Constants.ALERT_LIST, medListString)
                .putInt(Constants.ALERT_POSITION, position)
                .putInt("time_nw", timeNow.minute + timeNow.hour)
                .build()


            //set one time work request
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                MyWorkManager::class.java
            )
                .setInitialDelay(duration)
                .setInputData(data)
                .addTag("alarm")
                .build()
            WorkManager.getInstance().enqueue(oneTimeWorkRequest)
        }
    }

    private fun fetchData() {
        //call api
        preference = YourPreferences.getInstance(context)!!

        _irepo =  Repository.getInstance(
            WeatherClient.getInstance()!!,
            ConcreteLocalSource(context),
            context
        )

        CoroutineScope(Dispatchers.IO).launch {
            val lat = preference.getData(Constants.LAT)!!.toDouble()
            val long = preference.getData(Constants.LONG)!!.toDouble()
            val response = _irepo.getCurrentWeather(lat, long,
                getUnitSystem(context), getLang(context)
            )

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    //show alerts (description)
                    val desc = response.body()?.alerts?.get(1)?.description + response.body()?.alerts?.get(0)?.event
                    if (desc != null){
                        showNotification(context,"Weather Alerts", desc.toString(),100)
                        Log.d("alertsss!!",desc.toString())
                        Log.d("alertsss!!",response.body()?.alerts?.get(0)?.event.toString())
                    }
                    else{
                        showNotification(context,"Weather Alerts", "No Weather Alerts",100)
                    }
                }
            }
        }
    }
}