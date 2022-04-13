package com.example.weatherforecast.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Condition
import com.example.weatherforecast.utils.getTempUnit
import com.example.weatherforecast.utils.getWindUnit

class WeatherAdapter(var context: Context): RecyclerView.Adapter<WeatherAdapter.WeatherViewAdapter>() {

    private var conditionList = emptyList<Condition>()

    fun setConditions(conditions: List<Condition>) {
        this.conditionList = conditions
        notifyDataSetChanged()
    }

    inner class WeatherViewAdapter(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.desc_img)
        val name = itemView.findViewById<TextView>(R.id.name_text)
        val desc = itemView.findViewById<TextView>(R.id.desc_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewAdapter {
        val view = LayoutInflater.from(context).inflate(R.layout.card_layout_desc, parent, false)
        return WeatherViewAdapter(view)
    }

    override fun onBindViewHolder(holder: WeatherViewAdapter, position: Int) {
        holder.name.text = conditionList[position].name
        when(conditionList[position].name){
            context.getString(R.string.Pressure) ->  holder.desc.text = conditionList[position].des + "hPa"
            context.getString(R.string.Humidity) -> holder.desc.text = conditionList[position].des + "%"
            context.getString(R.string.Cloud) -> holder.desc.text = conditionList[position].des + "%"
            context.getString(R.string.Wind) -> holder.desc.text = conditionList[position].des + getWindUnit(context)
            context.getString(R.string.Feels_like) -> holder.desc.text = conditionList[position].des + getTempUnit(context)
            context.getString(R.string.Visiblity) -> holder.desc.text = conditionList[position].des + "meter"
        }

        Glide.with(context).load(conditionList[position].img).into(holder.img)

    }

    override fun getItemCount(): Int {
        return conditionList.size
    }
}