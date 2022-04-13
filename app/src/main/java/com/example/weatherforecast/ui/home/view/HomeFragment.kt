package com.example.weatherforecast.ui.home.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.*
import com.example.weatherforecast.network.WeatherClient
import com.example.weatherforecast.ui.home.viewmodel.HomeViewModel
import com.example.weatherforecast.ui.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.utils.*
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var city: TextView
    private lateinit var country: TextView
    private lateinit var degree: TextView
    private lateinit var degreeUnit: TextView
    private lateinit var description: TextView
    private lateinit var high_degree: TextView
    private lateinit var low_degree: TextView
    private lateinit var high_degree_sign:TextView
    private lateinit var low_degree_sign: TextView
    private lateinit var current_icon: ImageView
    private lateinit var weatherAdapter: WeatherAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var hourAdapter: HourAdapter
    lateinit var hour_recyclerView: RecyclerView
    private lateinit var weeklyAdapter: WeeklyAdapter
    lateinit var day_recyclerView: RecyclerView
    var PERMISSION_ID = 500
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    lateinit var viewModel: HomeViewModel
    lateinit var vmFactory: HomeViewModelFactory
    private var _binding: FragmentHomeBinding? = null
    private lateinit var yourPrefrence: YourPreferences


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        date = binding.date
        current_icon = binding.currentIcon
        time = binding.time
        city = binding.city
        country = binding.country
        degree = binding.degree
        degreeUnit = binding.degreeUnit
        high_degree_sign = binding.highDegreeSign
        low_degree_sign = binding.lowDegreeSign
        description = binding.description
        high_degree = binding.highDegree
        low_degree = binding.lowDegree
        recyclerView = binding.recyViewConditionDescription
        hour_recyclerView = binding.recyViewTodayForecast
        day_recyclerView = binding.recyclerView2

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //Getting ViewModel Ready
        vmFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(), ConcreteLocalSource(requireContext()),
                requireContext()
            ),requireContext())
        viewModel = ViewModelProvider(this, vmFactory).get(HomeViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        val current = LocalDateTime.now()
        val date_formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        val time_formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        date.text = current.format(date_formatter)
        time.text = current.format(time_formatter)

        yourPrefrence = YourPreferences.getInstance(requireContext())
    }

    override fun onResume() {
        super.onResume()
        yourPrefrence = YourPreferences.getInstance(requireContext())
        if(yourPrefrence.getData(Constants.LOCATION) == "2"){
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_home_to_mapsFragment)

        }else{
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc()
            }
            when(Navigation.findNavController(requireView()).previousBackStackEntry?.destination?.id){
                R.id.mapsFragment ->{
                    fetchLocationFromMaps()
                }
                else->{
                    fetchLocation()
            }
        }
    }
    }


    //to enable the location
    fun enableLoc() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun fetchLocationFromMaps(){
        //adding location from maps
        yourPrefrence = YourPreferences.getInstance(requireContext())
            val lat =yourPrefrence.getData(Constants.LAT)!!.toDouble()
            val long = yourPrefrence.getData(Constants.LONG)!!.toDouble()
            Log.d("====","fetchLocationFromMaps: lat"+lat)
            city.text = getCityName(lat,long)
            country.text =getCountryName(lat,long)
            viewModel.getCurrentWeather(lat,long, getUnitSystem(requireContext()), getLang(requireContext()))
            //Observe exposed data of viewModel
            viewModel.onlineWeather.observe(viewLifecycleOwner) { weather ->
                yourPrefrence.saveData(Constants.FromMaps,"false")
                bindHome(openWeatherJson = weather)
                bindCurrentConditions(weather.current)
                bindHours(weather.hourly)
                bindDays(weather.daily)
            }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation
        yourPrefrence = YourPreferences.getInstance(requireContext())

        task.addOnSuccessListener { location ->
            if (location != null) {
                if(yourPrefrence.getData(Constants.LOCATION).equals("3")){
                }else{
                    yourPrefrence.saveData(Constants.LAT,location.latitude.toString())
                    yourPrefrence.saveData(Constants.LONG,location.longitude.toString())
                }
                val lat = yourPrefrence.getData(Constants.LAT)!!.toDouble()
                val long = yourPrefrence.getData(Constants.LONG)!!.toDouble()
                Log.i("===","fetchLocation() lat:"+lat.toString())
                city.text = getCityName(lat,long)
                country.text =getCountryName(lat,long)
                viewModel.getCurrentWeather(lat,long, getUnitSystem(requireContext()), getLang(requireContext()))
                //Observe exposed data of viewModel
                viewModel.onlineWeather.observe(viewLifecycleOwner) { weather ->
                    bindHome(openWeatherJson = weather)
                    bindCurrentConditions(weather.current)
                    bindHours(weather.hourly)
                    bindDays(weather.daily)
                }
            }else{
                requestNewLocation()
            }
        }
    }

    fun requestNewLocation() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                var lastLocation: Location = locationResult.lastLocation

                yourPrefrence.saveData(Constants.LAT,lastLocation.latitude.toString())
                yourPrefrence.saveData(Constants.LONG,lastLocation.longitude.toString())
                val lat = yourPrefrence.getData(Constants.LAT)!!.toDouble()
                val long = yourPrefrence.getData(Constants.LONG)!!.toDouble()
                Log.d("LOCATION","Your current coordinates are: lat:"+lat+"long"+long +
                        " City: "+getCityName(lat,long) +
                        " Country: "+getCountryName(lat,long))
                city.text = getCityName(lat,long)
                country.text =getCountryName(lat,long)
                viewModel.getCurrentWeather(lat,long, getUnitSystem(requireContext()), getLang(requireContext()))
                viewModel.onlineWeather.observe(viewLifecycleOwner) { weather ->
                    Log.i("TAG", "onChanged: "+weather)
                    Log.i("TAG", "onCreate: ${weather.toString()}" )
                    bindHome(openWeatherJson = weather)
                    bindCurrentConditions(weather.current)
                    bindHours(weather.hourly)
                    bindDays(weather.daily)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!
        )
    }

    //function to get the city name
    private fun getCityName(lat: Double, long: Double): String{
        var cityName = ""
        var geocoder = Geocoder(requireContext(), Locale(getLang(requireContext())))
        var address: MutableList<Address> = geocoder.getFromLocation(lat,long,1)
        cityName = address.get(0).adminArea
        return cityName
    }

    //function to get the country name
    private fun getCountryName(lat: Double, long: Double): String{
        var countryName = ""
        var geocoder = Geocoder(requireContext(), Locale(getLang(requireContext())))
        var address: MutableList<Address> = geocoder.getFromLocation(lat,long,1)
        countryName = address.get(0).countryName
        return countryName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //binding home current conditions
    fun bindHome(openWeatherJson: OpenWeatherJson){
        date.text =convertToDate(openWeatherJson.current.dt)
        time.text = convertToTime(openWeatherJson.current.dt)
        degree.text = openWeatherJson.current.temp.toString()
        degreeUnit.text = getTempUnit(requireContext())
        high_degree.text = openWeatherJson.daily[0].temp!!.max.toString()
        high_degree_sign.text = "°" + getTempUnit(requireContext())
        low_degree.text = openWeatherJson.daily[0].temp!!.min.toString()
        low_degree_sign.text = "°" + getTempUnit(requireContext())
        description.text = openWeatherJson.current.weather[0].description
        var iconurl = "http://openweathermap.org/img/wn/${openWeatherJson.current.weather[0].icon}@2x.png";
        Glide.with(requireContext()).load(iconurl).apply(
            RequestOptions().override(200, 200).placeholder(R.drawable.ic_launcher_background)
        ).into(current_icon)
    }

    //binding current conditions
    fun bindCurrentConditions(current: Current){
        weatherAdapter.setConditions(listOf<Condition>(
            Condition(R.drawable.ic_pressure,current.pressure.toString(),getString(R.string.Pressure)),
            Condition(R.drawable.ic_humidity,current.humidity.toString(),getString(R.string.Humidity)),
            Condition(R.drawable.ic_cloud,current.clouds.toString(),getString(R.string.Cloud)),
            Condition(R.drawable.ic_wind,current.windSpeed.toString(),getString(R.string.Wind)),
            Condition(R.drawable.ic_feels_like,current.feelsLike.toString(),getString(R.string.Feels_like)),
            Condition(R.drawable.ic_visibility,current.visibility.toString(),getString(R.string.Visiblity))
        ))
        weatherAdapter.notifyDataSetChanged()
    }

    //binding hours in the day
    fun bindHours(hourly: List<Hourly>){
        hourAdapter.setWeatherHours(hourly)
        hourAdapter.notifyDataSetChanged()

    }

    //binding days in the week
    fun bindDays(daily: List<Daily>){
        weeklyAdapter.setWeatherDays(daily)
        weeklyAdapter.notifyDataSetChanged()
    }


    fun convertToTime(dt: Long): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("h:mm a", Locale(getLang(requireContext())))
        return format.format(date)
    }

    fun convertToDate(dt: Long): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("EEE, MMM d, ''yy", Locale(getLang(requireContext())))
        return format.format(date)
    }
}