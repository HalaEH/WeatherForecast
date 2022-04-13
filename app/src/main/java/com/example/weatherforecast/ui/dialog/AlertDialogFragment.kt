package com.example.weatherforecast.ui.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.weatherforecast.databinding.FragmentAlertDialogBinding
import com.example.weatherforecast.ui.alerts.view.AlertCommunicator
import com.example.weatherforecast.utils.YourPreferences
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlertDialogFragment(val alertComm: AlertCommunicator) : DialogFragment(){

    lateinit var binding: FragmentAlertDialogBinding
    private lateinit var yourPrefrence: YourPreferences
    var fromDateMillis = 0L
    var toDateMillis = 0L
    var schedYear = 0
    var schedMonth = 0
    var schedDay = 0
    var schedHour = 0
    var schedMinute = 0
    lateinit var time : String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        dialog!!.window!!
        isCancelable = false
        // Inflate the layout for this fragment
        return binding.root
      }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPrefrence = YourPreferences.getInstance(requireContext())
        binding.fromDate.setOnClickListener{
            datePicker(true)
        }
        dialog!!.show()
        var window = dialog!!.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        binding.toDate.setOnClickListener{
            datePicker(false)
        }

        dialog!!.show()
        window = dialog!!.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.timeBtn.setOnClickListener{
            timePicker()
        }

        binding.saveBtn.setOnClickListener{
            alertComm.alarm(fromDateMillis,toDateMillis,time)
        }
    }

    private fun datePicker(isFrom: Boolean) {
        val calendar = Calendar.getInstance()
        var mYear = calendar[Calendar.YEAR]
        var mMonth = calendar[Calendar.MONTH]
        var mDay = calendar[Calendar.DAY_OF_MONTH]

        //show dialog
        val datePickerDialog = DatePickerDialog(
            requireContext()!!,
            { view, year, month, dayOfMonth ->

                var format = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                calendar.set(year, month, dayOfMonth);
                val strDate = format.format(calendar.time)
                Log.d("dateeeee",strDate)
                if(isFrom == true){
                    fromDateMillis = getMilliFromDate(strDate)
                    Log.d("DIALOG",fromDateMillis.toString())
                    binding.fromDate.text = strDate.toString()

                }else{
                    toDateMillis = getMilliFromDate(strDate)
                    Log.d("DIALOG",toDateMillis.toString())
                    binding.toDate.text = strDate.toString()
                }
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    fun getMilliFromDate(dateFormat: String?): Long {
        var milliseconds = 0L
        val f = SimpleDateFormat("dd-MMM-yyyy")
        try {
            val d = f.parse(dateFormat)
            milliseconds = d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }


    private fun timePicker() {

        val mTimePicker: TimePickerDialog
        val calendar = Calendar.getInstance()
        val mHour = calendar[Calendar.HOUR_OF_DAY]
        val mMinute = calendar[Calendar.MINUTE]

        mTimePicker =
            TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    time = "$hourOfDay:$minute"
                    binding.timeBtn.text = "${hourOfDay%12} : $minute "+ if (hourOfDay >= 12) "PM" else "AM"
                }
            }, mHour, mMinute, false)

        mTimePicker.show()

//
//        // Get Current Time
//        val c = Calendar.getInstance()
//        val hour = c[Calendar.HOUR_OF_DAY]
//        val minute = c[Calendar.MINUTE]
//
//        // Launch Time Picker Dialog
//        val timePickerDialog = TimePickerDialog(requireContext(),
//            { view, hourOfDay, minute ->
//                schedHour = hourOfDay
//                schedMinute = minute
//                val dateStr =
//                    "$schedDay-$schedMonth-$schedYear/$schedHour:$schedMinute"
//                val sdf = SimpleDateFormat("dd-M-yyyy/HH:mm")
//                try {
//                    val date = sdf.parse(dateStr)
//                    val calendar = Calendar.getInstance()
//                    calendar.time = date
//                    val schedMillis = calendar.timeInMillis
//                    val currDate = Date()
//                    val currMillis = currDate.time
//                    val delayInMillis = schedMillis - currMillis
//                    _time = delayInMillis
//                    binding.timeBtn.text = "${schedHour%12} : $schedMinute "+ if (schedHour >= 12) "PM" else "AM"
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
//            }, hour, minute, false
//        )
//        timePickerDialog.show()
    }

//    private fun getDateTimeCalendar(){
//        val cal: Calendar = Calendar.getInstance()
//        day = cal.get(Calendar.DAY_OF_MONTH)
//        month = cal.get(Calendar.MONTH)
//        year = cal.get(Calendar.YEAR)
//        hour = cal.get(Calendar.HOUR)
//        minute = cal.get(Calendar.MINUTE)
//    }
//
//    private fun pickDate(){
//        getDateTimeCalendar()
//        DatePickerDialog(requireContext(),this,year,month,day).show()
//
//    }
//
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//        schedYear = year
//        schedMonth = month
//        schedDay = day
//        getDateTimeCalendar()
//        TimePickerDialog(requireContext(),this,hour,minute,true).show()
//    }
//
//    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
//        schedHour = hour
//        schedMinute = minute
//    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }




}