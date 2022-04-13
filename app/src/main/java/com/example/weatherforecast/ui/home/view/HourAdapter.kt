package com.example.weatherforecast.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.utils.getTempUnit
import java.text.SimpleDateFormat
import java.util.*

class HourAdapter(var context: Context): RecyclerView.Adapter<HourAdapter.DayViewHolder>() {

    private var weatherHours = emptyList<Hourly>()

    fun setWeatherHours(weatherHours: List<Hourly>) {
        this.weatherHours = weatherHours
        notifyDataSetChanged()
    }

    inner class DayViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time)
        val img: ImageView = itemView.findViewById(R.id.pic)
        val degree: TextView = itemView.findViewById(R.id.degree_txt)
        val day_cv: CardView = itemView.findViewById(R.id.day_cardView)
        var degree_unit = itemView.findViewById<TextView>(R.id.degree_unit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hour_row, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.time.text = convertToTime(weatherHours[position].dt,context)
        holder.degree.text = weatherHours[position].temp.toString()
        holder.degree_unit.text = getTempUnit(context)
        var iconurl = "http://openweathermap.org/img/wn/${weatherHours[position].weather[0].icon}@2x.png";
        Glide.with(context).load(iconurl).apply(
            RequestOptions().override(200, 200).placeholder(R.drawable.ic_launcher_background)
        ).into(holder.img)

    }

    fun convertToTime(dt: Long, context: Context): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    override fun getItemCount(): Int {
        return weatherHours.size
    }
}