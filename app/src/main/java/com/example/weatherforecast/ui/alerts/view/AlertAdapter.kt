package com.example.weatherforecast.ui.alerts.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.model.WeatherAlert
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter (var context: Context,private val listener: OnDeleteAlertClickListener): RecyclerView.Adapter<AlertAdapter.AlertViewAdapter>() {

    private var alertList = ArrayList<WeatherAlert>()

    fun setAlerts(alertList: List<WeatherAlert>) {
        this.alertList.apply {
            clear()
            addAll(alertList)
            notifyDataSetChanged()
        }
    }

    inner class AlertViewAdapter(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val from_txt = itemView.findViewById<TextView>(R.id.from_txt)
        val to_txt = itemView.findViewById<TextView>(R.id.to_txt)
        val delete_icon = itemView.findViewById<ImageView>(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewAdapter {
        val view = LayoutInflater.from(context).inflate(R.layout.alert_row, parent, false)
        return AlertViewAdapter(view)
    }

    override fun onBindViewHolder(holder: AlertViewAdapter, position: Int) {
        holder.from_txt.text = convertFromLongToDate(alertList[position].startDay,context)
        holder.to_txt.text = convertFromLongToDate(alertList[position].endDay,context)
        holder.delete_icon.setOnClickListener{listener.onClick(alertList[position])

        }
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    private fun convertFromLongToDate(day: Long,context:Context): String {
        var date: String? = null
        val formatter = SimpleDateFormat("d MMM YYYY") // modify format
        date = formatter.format(Date(day))
        println("Today is $date")
        return date

//        val date = Date(day * 1000)
//        val format = SimpleDateFormat("d MMM YYYY", Locale.ENGLISH)
//        return format.format(date)
    }

}