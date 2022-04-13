package com.example.weatherforecast.ui.favoriteConditions.view


import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavConditionsBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.*
import com.example.weatherforecast.network.WeatherClient
import com.example.weatherforecast.ui.favoriteConditions.viewmodel.FavConditionViewModel
import com.example.weatherforecast.ui.favoriteConditions.viewmodel.FavConditionViewModelFactory
import com.example.weatherforecast.ui.home.view.HourAdapter
import com.example.weatherforecast.ui.home.view.WeatherAdapter
import com.example.weatherforecast.ui.home.view.WeeklyAdapter
import com.example.weatherforecast.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import java.util.*


class FavConditionsFragment : Fragment() {

    private lateinit var yourPrefrence: YourPreferences
    private lateinit var city: TextView
    private lateinit var country: TextView
    private lateinit var degree: TextView
    private lateinit var description: TextView
    private lateinit var high_degree: TextView
    private lateinit var low_degree: TextView
    private lateinit var current_icon: ImageView
    private lateinit var weatherAdapter: WeatherAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var hourAdapter: HourAdapter
    lateinit var hour_recyclerView: RecyclerView
    private lateinit var weeklyAdapter: WeeklyAdapter
    lateinit var day_recyclerView: RecyclerView
    lateinit var degreeUnit: TextView
    lateinit var viewModel: FavConditionViewModel
    lateinit var vmFactory: FavConditionViewModelFactory
    private var _binding: FragmentFavConditionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //Getting ViewModel Ready
        vmFactory = FavConditionViewModelFactory(Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext()), requireContext()),requireContext())
        viewModel = ViewModelProvider(this, vmFactory).get(FavConditionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavConditionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        current_icon = binding.currentIcon
        city = binding.city
        country = binding.country
        degree = binding.degree
        degreeUnit = binding.degreeUnit
        description = binding.description
        high_degree = binding.highDegree
        low_degree = binding.lowDegree
        recyclerView = binding.recyViewConditionDescription
        hour_recyclerView = binding.recyViewTodayForecast
        day_recyclerView = binding.recyclerView2

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPrefrence = YourPreferences.getInstance(requireContext())

        //Getting Recycler View related items ready
        weatherAdapter = WeatherAdapter(requireContext())
        gridLayoutManager =  GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = weatherAdapter

        hourAdapter = HourAdapter(requireContext())
        gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        hour_recyclerView.layoutManager = gridLayoutManager
        hour_recyclerView.adapter = hourAdapter

        weeklyAdapter = WeeklyAdapter(requireContext())
        gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
        day_recyclerView.layoutManager = gridLayoutManager
        day_recyclerView.adapter = weeklyAdapter

        val flag = initFavSharedPref(requireContext()).getInt(getString(R.string.FAV_FLAG), 0)
        if (flag == 1 &&
            Navigation.findNavController(requireView()).previousBackStackEntry?.destination?.id == R.id.nav_gallery
        ) {

            //require get Data
            val lat = initFavSharedPref(requireContext()).getFloat(getString(R.string.LAT), 0f)
            val long = initFavSharedPref(requireContext()).getFloat(getString(R.string.LON), 0f)
            initFavSharedPref(requireContext()).edit().apply {
                putInt(getString(R.string.FAV_FLAG), 0)
                apply()
            }

            viewModel.getCurrentWeather(lat!!.toDouble(), long!!.toDouble(), getUnitSystem(requireContext()),
                getLang(requireContext()))
            viewModel.onlineWeather.observe(viewLifecycleOwner) {weather->
                bindFav(openWeatherJson = weather)
                bindCurrentConditions(weather.current)
                bindHours(weather.hourly)
                bindDays(weather.daily)
            }
            initFavSharedPref(requireContext()).edit().apply {
               // putString(yourPrefrence.getData(Constants.FAV_FLAG), "0")
                putInt(getString(R.string.FAV_FLAG), 0)
                apply()
            }
        }}

        //binding fav current conditions
        fun bindFav(openWeatherJson: OpenWeatherJson) {
            city.text = getCityText(openWeatherJson.lat,openWeatherJson.lon,openWeatherJson.timezone)
            country.text = getCountryText(openWeatherJson.lat,openWeatherJson.lon,openWeatherJson.timezone)
            degree.text = openWeatherJson.current.temp.toString()
            degreeUnit.text = getTempUnit(requireContext())
            high_degree.text = openWeatherJson.daily[0].temp!!.max.toString()
            low_degree.text = openWeatherJson.daily[0].temp!!.min.toString()
            description.text = openWeatherJson.current.weather[0].description
            var iconurl =
                "http://openweathermap.org/img/wn/${openWeatherJson.current.weather[0].icon}@2x.png";
            Glide.with(requireContext()).load(iconurl).apply(
                RequestOptions().override(200, 200).placeholder(R.drawable.ic_launcher_background)
            ).into(current_icon)
        }

        //binding current conditions
        fun bindCurrentConditions(current: Current) {
            Log.d("cloudsssssss",current.clouds.toString())
            weatherAdapter.setConditions(
                listOf<Condition>(
                    Condition(R.drawable.ic_pressure, current.pressure.toString() , getString(R.string.Pressure)),
                    Condition(R.drawable.ic_humidity, current.humidity.toString(), getString(R.string.Humidity)),
                    Condition(R.drawable.ic_cloud, current.clouds.toString(), getString(R.string.Cloud)),
                    Condition(R.drawable.ic_wind, current.windSpeed.toString(), getString(R.string.Wind)),
                    Condition(R.drawable.ic_feels_like, current.feelsLike.toString(), getString(R.string.Feels_like)),
                    Condition(R.drawable.ic_visibility, current.visibility.toString(), getString(R.string.Visiblity))
                )
            )
            weatherAdapter.notifyDataSetChanged()
        }

        //binding hours in the day
        fun bindHours(hourly: List<Hourly>) {
            hourAdapter.setWeatherHours(hourly)
            hourAdapter.notifyDataSetChanged()
        }

        //binding days in the week
        fun bindDays(daily: List<Daily>) {
            weeklyAdapter.setWeatherDays(daily)
            weeklyAdapter.notifyDataSetChanged()
        }


    private fun getCityText(lat: Double, lon: Double, timezone: String): String {
        var city = "city"
        val geocoder = Geocoder(requireContext(), Locale(getLang(requireContext())))
        val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            city = "$state"
            if (state.isNullOrEmpty()) city = country
        }
        if (city == "city")
            city = timezone
        return city
    }

    private fun getCountryText(lat:Double,lon: Double,timezone: String): String{
        var country = "country"
        val geocoder = Geocoder(requireContext(), Locale(getLang(requireContext())))
        val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            val address = addresses[0].countryName
            country = "$address"
        }
        if (country == "country")
            country = timezone
        return country
    }

}