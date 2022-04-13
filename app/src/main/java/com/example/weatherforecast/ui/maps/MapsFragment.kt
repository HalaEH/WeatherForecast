package com.example.weatherforecast.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapsBinding
import com.example.weatherforecast.utils.Constants
import com.example.weatherforecast.utils.YourPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(),OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    var currentMarker: Marker? = null
    private lateinit var mMap: GoogleMap
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 101
    private lateinit var yourPrefrence: YourPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fetchLocation()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    private fun fetchLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    requireContext(),
                    currentLocation!!.latitude.toString() + "" + currentLocation!!.longitude,
                    Toast.LENGTH_SHORT
                ).show()
                val supportMapFragment =
                    (childFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        moveMarket(latLng)

        googleMap.setOnMapClickListener {
            var lat: Float = 0f
            var long: Float = 0f
            val marker = MarkerOptions().apply {
                position(it)
                lat = it.latitude.toFloat()
                long = it.longitude.toFloat()
                title((lat).plus(long).toString())
                googleMap.clear()
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it, 10f
                    )
                )
            }
            googleMap.addMarker(marker)

            binding.saveBtn.setOnClickListener {
                yourPrefrence = YourPreferences.getInstance(requireContext())
                when(Navigation.findNavController(requireView()).previousBackStackEntry?.destination?.id){
                    R.id.nav_gallery ->{
                        //favorite
                        yourPrefrence.saveData(Constants.FAV_LAT,lat.toString())
                        yourPrefrence.saveData(Constants.FAV_LON,long.toString())
                        yourPrefrence.saveData(Constants.FromMaps,"true")
                        Log.d("====", "Fav latitude: " + yourPrefrence.getData(Constants.FAV_LAT))
                        Log.d("====", "Fav longitude : " + yourPrefrence.getData(Constants.FAV_LON))
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mapsFragment_to_favouriteFragment)
                    }
                else->{
                    //home
                    yourPrefrence.saveData(Constants.LAT,lat.toString())
                    yourPrefrence.saveData(Constants.LONG,long.toString())
                    yourPrefrence.saveData(Constants.FromMaps,"true")
                    yourPrefrence.saveData(Constants.LOCATION,"3")
                    Log.d("====", "latitude: " + yourPrefrence.getData(Constants.LAT))
                    Log.d("====", "longitude : " + yourPrefrence.getData(Constants.LONG))
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_mapsFragment_to_navigation_home)
                }
            }
        }

    }
    }

    private fun moveMarket(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("I am here")
            .snippet(getTheAddress(latLng!!.latitude, latLng!!.longitude)).draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun getTheAddress(latitude: Double, longitude: Double): String? {
        var retVal = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            retVal = addresses[0].getAddressLine(0)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return retVal
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }


}