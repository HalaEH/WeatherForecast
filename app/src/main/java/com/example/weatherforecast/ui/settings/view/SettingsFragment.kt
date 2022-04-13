package com.example.weatherforecast.ui.settings.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.model.Languages
import com.example.weatherforecast.model.Units
import com.example.weatherforecast.ui.settings.viewmodel.SettingsViewModel
import com.example.weatherforecast.utils.*
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var yourPrefrence: YourPreferences


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        //location
//        binding.locationRG.setOnClickListener{
//            if(binding.radioButtonGPS.isChecked){
//                yourPrefrence = YourPreferences.getInstance(requireContext())
//                yourPrefrence.saveData(Constants.LOCATION,"1")
//            }else if(binding.radioButtonMaps.isChecked){
//                yourPrefrence = YourPreferences.getInstance(requireContext())
//                yourPrefrence.saveData(Constants.LOCATION,"2")
//                Navigation.findNavController(requireView())
//                    .navigate(R.id.action_settings_to_mapsFragment)
//            }
//        }

        //language
        binding.langRG.setOnClickListener{
            if(binding.radioButtonEnglish.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.LANGUAGE,"1")
            }else if(binding.radioButtonArabic.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.LANGUAGE,"2")
            }
        }


        //notification
        binding.notificationRG.setOnClickListener{
            if(binding.radioButtonEnable.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.NOTIFICATION,"1")
            }else if(binding.radioButtonDisable.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.NOTIFICATION,"2")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        handleRadioButton(requireContext())
        //handle change in location (Maps)
        binding.radioButtonMaps.setOnClickListener{
            if(checkForInternet(requireContext())){
                if(binding.radioButtonMaps.isChecked){
                    yourPrefrence = YourPreferences.getInstance(requireContext())
                    yourPrefrence.saveData(Constants.LOCATION,"2")
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_settings_to_mapsFragment)
                }
            }else{
            Toast.makeText(requireContext(),"Internet Disconnected!",Toast.LENGTH_LONG).show()
            }
        }

        //handle change in location (GPS)
        binding.radioButtonGPS.setOnClickListener{
            if(checkForInternet(requireContext())){
                if(binding.radioButtonGPS.isChecked){
                    yourPrefrence = YourPreferences.getInstance(requireContext())
                    yourPrefrence.saveData(Constants.LOCATION,"1")
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_settings_to_nav_home)
                }
            } else{
            Toast.makeText(requireContext(),"Internet Disconnected!",Toast.LENGTH_LONG).show()
            }
        }

        // handle change in Temperature and wind speed
        binding.radioButtonCelsius.setOnClickListener {
            onTempUnitsClicked(it)
        }
        binding.radioButtonKelvin.setOnClickListener {
            onTempUnitsClicked(it)
        }
        binding.radioButtonFahrenheit.setOnClickListener {
            onTempUnitsClicked(it)
        }

        //handle change in Lang
        binding.radioButtonArabic.setOnClickListener{
            onLangUnitsClicked(it)
        }
        binding.radioButtonEnglish.setOnClickListener{
            onLangUnitsClicked(it)
        }
    }

    private fun handleRadioButton(context: Context) {
        handleTempUnit(context)
        handleLang(context)
    }

    private fun onTempUnitsClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            if(checkForInternet(requireContext())){
                when (view.getId()) {
                    R.id.radio_button_Celsius ->
                        if (checked) {
                            initUNIT(Units.METRIC.name, requireContext())
                        }
                    R.id.radio_button_Kelvin ->
                        if (checked) {
                            initUNIT(Units.STANDARD.name, requireContext())
                        }
                    R.id.radio_button_Fahrenheit ->
                        if (checked) {
                            initUNIT(Units.IMPERIAL.name, requireContext())
                        }
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Internet Disconnected",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun handleTempUnit(context: Context) {
        when (getUnitSystem(requireContext())) {
            Units.METRIC.name -> {
                updateTempUnit(true)
            }
            Units.IMPERIAL.name -> {
                updateTempUnit(imperial = true)
            }
            Units.STANDARD.name -> {
                updateTempUnit(standard = true)
            }
        }
    }


    private fun updateTempUnit(
        metric: Boolean = false,
        imperial: Boolean = false,
        standard: Boolean = false
    ) {
        binding.radioButtonCelsius.isChecked = metric
        binding.radioButtonFahrenheit.isChecked = imperial
        binding.radioButtonKelvin.isChecked = standard
    }


    private fun onLangUnitsClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            if(checkForInternet(requireContext())){
                when (view.getId()) {
                    R.id.radio_button_Arabic ->
                        if (checked) {
                            initLANG("ar", requireContext())
                            val metric = resources.displayMetrics
                            val configuration = resources.configuration
                            configuration.locale = Locale("ar")
                            Locale.setDefault(Locale("ar"))
                            configuration.setLayoutDirection(Locale("ar"))
                            resources.updateConfiguration(configuration, metric)
                            onConfigurationChanged(configuration)
                            Log.d("SETTINGS", "ARABIC"+ getLang(requireContext()))
                        }
                    R.id.radio_button_English ->
                        if (checked) {
                            initLANG("en", requireContext())
                            val metric = resources.displayMetrics
                            val configuration = resources.configuration
                            configuration.locale = Locale("en")
                            Locale.setDefault(Locale("en"))
                            configuration.setLayoutDirection(Locale("en"))
                            resources.updateConfiguration(configuration, metric)
                            onConfigurationChanged(configuration)
                            Log.d("SETTINGS", "ENGLISH"+ getLang(requireContext()))

                        }
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Internet Disconnected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activity?.finish()
        activity?.startActivity(activity?.intent)
    }



    private fun handleLang(context: Context) {
        when (getUnitSystem(requireContext())) {
            Languages.ARABIC.name -> {
                updateLang(arabic = true)
            }
            Languages.ENGLISH.name -> {
                updateLang(english = true)
            }
        }
    }



    private fun updateLang(arabic: Boolean = false,english: Boolean = false) {
        binding.radioButtonEnglish.isChecked = english
        binding.radioButtonArabic.isChecked = arabic

    }
}