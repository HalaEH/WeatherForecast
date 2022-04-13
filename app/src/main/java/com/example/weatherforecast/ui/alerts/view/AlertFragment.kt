package com.example.weatherforecast.ui.alerts.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.WeatherAlert
import com.example.weatherforecast.network.WeatherClient
import com.example.weatherforecast.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherforecast.ui.alerts.viewmodel.AlertViewModelFactory
import com.example.weatherforecast.ui.dialog.AlertDialogFragment
import com.example.weatherforecast.utils.Constants
import com.example.weatherforecast.utils.checkForInternet
import com.example.weatherforecast.workmanager.MyWorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AlertFragment : Fragment(),AlertCommunicator,OnDeleteAlertClickListener {

    lateinit var binding: FragmentAlertBinding
    lateinit var alert_rv: RecyclerView
    lateinit var adapter: AlertAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var viewModel: AlertViewModel
    lateinit var viewModelAlert: AlertViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var weatherAlert: WeatherAlert ?= null
    lateinit var alertDialog: AlertDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        alert_rv = binding.alertRv

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModelAlert = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(), ConcreteLocalSource(requireContext()),
                requireContext()
            ))
        viewModel = ViewModelProvider(this, viewModelAlert).get(AlertViewModel::class.java)
        alertDialog = AlertDialogFragment(this)
        binding.addAlertFloatBtn.setOnClickListener{
            if(checkForInternet(requireContext())){
                alertDialog.show(requireFragmentManager(),"Dialog")
            }else{
                Toast.makeText(requireContext(),"Internet Disconnected!", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AlertAdapter(requireContext(),this)
        layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        alert_rv.layoutManager = layoutManager
        alert_rv.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        viewModel.getWeatherAlerts()
        viewModel.getWeatherAlerts().observe(viewLifecycleOwner) {
            if(checkForInternet(requireContext())){
                setAlarm(it)
            }
            bindingFragment(it)
        }
    }

    private fun bindingFragment(alertWeathers: List<WeatherAlert>) {
        adapter.setAlerts(alertWeathers)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setAlarm(alerts: List<WeatherAlert>?) {
        WorkManager.getInstance().cancelAllWorkByTag("alarm")
        if (alerts != null && !alerts.isEmpty()) {
            val gson = Gson()
            val listString = gson.toJson(alerts)
            val timeNow = LocalDateTime.now()

            var timeAt = LocalDate.now().atTime(
                Integer.valueOf(alerts[0].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                Integer.valueOf(
                    alerts[0].time.split(":").toTypedArray()[1].trim { it <= ' ' })
            ) //hours minutes
            var duration = Duration.between(timeNow, timeAt)
            var position = 0
            for (i in alerts.indices) {
                timeAt = LocalDate.now().atTime(
                    Integer.valueOf(alerts[i].time.split(":").toTypedArray()[0].trim { it <= ' ' }),
                    Integer.valueOf(
                        alerts[i].time.split(":").toTypedArray()[1].trim { it <= ' ' })
                )
                if (Calendar.getInstance().time.time >= alerts[i].startDay!! && Calendar.getInstance().time.time <= alerts[i].endDay!!) {
                    if (timeAt.isAfter(timeNow) && duration.abs().toMillis() > Duration.between(
                            timeNow,
                            timeAt
                        ).toMillis()
                    ) {
                        duration = Duration.between(timeNow, timeAt)
                        Log.d("delayyy",timeNow.toString())
                        Log.d("delayyy",timeAt.toString())

                        Log.d("delayyy",duration.toString())
                        position = i
                    }
                }
            }
            val data = Data.Builder()
                .putString(Constants.ALERT_LIST, listString)
                .putInt(Constants.ALERT_POSITION, position)
                .putInt("time_nw", timeNow.minute + timeNow.hour)
                .build()


            //set one time work request
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                MyWorkManager::class.java
            )
                .setInitialDelay(duration)
                .setInputData(data)
                .addTag("alarm")
                .build()
            WorkManager.getInstance().enqueue(oneTimeWorkRequest)
        }
    }

    override fun alarm(fromDate: Long, toDate: Long, time: String) {
        Log.d("alert",fromDate.toString())
        Log.d("alert",toDate.toString())
        viewModel.insertAlert(WeatherAlert((toDate+fromDate).toInt(),time = time,startDay = fromDate,endDay = toDate))
        alertDialog.dismiss()
    }

    override fun onClick(alert: WeatherAlert) {
        viewModel.deleteAlert(alert)
        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
    }

}