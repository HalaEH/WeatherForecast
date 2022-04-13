package com.example.weatherforecast.ui.home.view

import android.content.Context
import android.media.Image
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Daily
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.utils.getLang
import java.text.SimpleDateFormat
import java.util.*

class WeeklyAdapter (var context: Context): RecyclerView.Adapter<WeeklyAdapter.WeekViewHolder>() {

    private var weatherDays = emptyList<Daily>()

    fun setWeatherDays(weatherDays: List<Daily>) {
        this.weatherDays = weatherDays
        notifyDataSetChanged()
    }

    inner class WeekViewHolder (private val itemView: View): RecyclerView.ViewHolder(itemView){
        val day_txt: TextView = itemView.findViewById(R.id.day_txt)
        val weather_icon: ImageView = itemView.findViewById(R.id.weather_icon)
        val desc: TextView = itemView.findViewById(R.id.desc)
//        val low_degree: TextView = itemView.findViewById(R.id.low_degree)
//        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
//        val high_degree: TextView = itemView.findViewById(R.id.high_degree)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.week_row, parent, false)
        return WeekViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.day_txt.text = convertToDay(weatherDays[position].dt,context)
        holder.desc.text = weatherDays[position].weather[0].description
//        holder.low_degree.text = weatherDays[position].temp!!.min.toString()
//        holder.high_degree.text = weatherDays[position].temp!!.max.toString()
//        var max = weatherDays[0].temp!!.max
//        var min = weatherDays[0].temp!!.min
//        for(i in 0..weatherDays.size-1){
//            if(weatherDays[i].temp!!.max!! > max!!){
//                max = weatherDays[i].temp!!.max!!
//            }
//            if(weatherDays[i].temp!!.min!! < min!!){
//                min = weatherDays[i].temp!!.max!!
//            }
//        }
//        holder.seekBar.min = min!!.toInt()
//        holder.seekBar.max = max!!.toInt()
//        holder.seekBar.progress = weatherDays[position].temp!!.max!!.toInt()
        var iconurl = "http://openweathermap.org/img/wn/${weatherDays[position].weather[0].icon}@2x.png";
        Glide.with(context).load(iconurl).apply(
            RequestOptions().override(200, 200).placeholder(R.drawable.ic_launcher_background)
        ).into(holder.weather_icon)
    }

    fun convertToDay(dt: Long, context: Context): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("EEEE",Locale(getLang(context)))
        return format.format(date)
    }

    override fun getItemCount(): Int {
        return weatherDays.size
    }
}