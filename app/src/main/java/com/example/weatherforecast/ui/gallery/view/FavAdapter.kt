package com.example.weatherforecast.ui.gallery.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.utils.Constants
import com.example.weatherforecast.utils.YourPreferences
import com.example.weatherforecast.utils.checkForInternet
import com.example.weatherforecast.utils.initFavSharedPref

class FavAdapter(var context: Context,private val listener: OnFavClickListener
): RecyclerView.Adapter<FavAdapter.FavViewAdapter>()  {

    private var favList = emptyList<OpenWeatherJson>()
    private lateinit var yourPrefrence: YourPreferences
    lateinit var view: View

    fun setFavList(favList: List<OpenWeatherJson>) {
        this.favList = favList
        notifyDataSetChanged()
    }

    inner class FavViewAdapter(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val cityName_txt = itemView.findViewById<TextView>(R.id.cityName_txt)
        val delete_icon = itemView.findViewById<ImageView>(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewAdapter {
        view = LayoutInflater.from(context).inflate(R.layout.fav_row, parent, false)
        return FavViewAdapter(view)
    }

    override fun onBindViewHolder(holder: FavViewAdapter, position: Int) {
        holder.cityName_txt.text = favList[position].timezone
        holder.cityName_txt.setOnClickListener{
            yourPrefrence = YourPreferences.getInstance(context)
            initFavSharedPref(context)
                .edit()
                .apply {
                    putFloat(context.getString(R.string.LON), favList[position].lon.toFloat())
                    putFloat(context.getString(R.string.LAT), favList[position].lat.toFloat())

                    putInt(context.getString(R.string.ID), favList[position].id)
                    putInt(context.getString(R.string.FAV_FLAG), 1)
                    apply()
                }
            if(checkForInternet(context)){
                Navigation.findNavController(view).navigate(R.id.action_favouriteFragment_to_favConditions_fragment)
            }else{
                Toast.makeText(context,"Internet Disconnected!",Toast.LENGTH_LONG).show()
            }
        }
        holder.delete_icon.setOnClickListener{listener.onClick(favList[position])}
    }

    override fun getItemCount(): Int {
        return favList.size
    }
}