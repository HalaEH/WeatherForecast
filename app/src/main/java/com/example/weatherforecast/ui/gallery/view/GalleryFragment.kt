package com.example.weatherforecast.ui.gallery.view

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentGalleryBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.OpenWeatherJson
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.network.WeatherClient
import com.example.weatherforecast.ui.gallery.viewmodel.FavoriteViewModel
import com.example.weatherforecast.ui.gallery.viewmodel.FavoriteViewModelFactory
import com.example.weatherforecast.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GalleryFragment : Fragment(), OnFavClickListener {

    private var _binding: FragmentGalleryBinding? = null
    lateinit var fav_rv: RecyclerView
    lateinit var adapter: FavAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var viewModel: FavoriteViewModel
    lateinit var viewModelFavoriteViewModel: FavoriteViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var currentLocation: Location? = null
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var yourPrefrence: YourPreferences



    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     //   val galleryViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        fav_rv = binding.favRv
        val root: View = binding.root

        binding.addFavFloatBtn.setOnClickListener{
            if(checkForInternet(requireContext())){
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_favouriteFragment_to_mapsFragment)
            }else{
                Toast.makeText(requireContext(),"Internet Disconnected!",Toast.LENGTH_LONG).show()
            }

        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModelFavoriteViewModel = FavoriteViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),ConcreteLocalSource(requireContext()),
                requireContext()
            ),requireContext())
        viewModel = ViewModelProvider(this, viewModelFavoriteViewModel).get(FavoriteViewModel::class.java)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FavAdapter(requireContext(),this)
        layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        fav_rv.layoutManager = layoutManager
        fav_rv.adapter = adapter
  //      sharedPreferences = initSharedPref(requireContext())
    }

    override fun onResume() {
        super.onResume()
        //adding location from maps
        yourPrefrence = YourPreferences.getInstance(requireContext())
        Log.d("===","Fav: from maps"+yourPrefrence.getData(Constants.FromMaps))
        if(yourPrefrence.getData(Constants.FromMaps).equals("true")){
            val lat =yourPrefrence.getData(Constants.FAV_LAT)!!.toDouble()
            val long = yourPrefrence.getData(Constants.FAV_LON)!!.toDouble()
            Log.d("====","lat"+lat)
            viewModel.getFavWeather(lat, long, getUnitSystem(requireContext()),"en")
            viewModel.localFavWeather().observe(viewLifecycleOwner){weather->
                yourPrefrence.saveData(Constants.FromMaps,"false")
                for( i in 0..weather.size-1){
                    Log.d("====","Faaaav"+weather[i].toString())
                }
                bindingFavoriteWeather(weather)
            }
        }else{
            viewModel.localFavWeather()
            viewModel.localFavWeather().observe(viewLifecycleOwner){ weather ->
                yourPrefrence.saveData(Constants.FromMaps, "false")
                for (i in 0..weather.size - 1) {
                    Log.d("====", "Faaaav" + weather[i].toString())
                }
                bindingFavoriteWeather(weather)
            }
        }

    }


    private fun bindingFavoriteWeather(favoriteWeather: List<OpenWeatherJson>){
        adapter.setFavList(favoriteWeather)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(favWeather: OpenWeatherJson) {
        viewModel.deleteFav(favWeather)
        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()

    }
}