package com.example.weatherforecast.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import androidx.fragment.app.DialogFragment
import com.example.weatherforecast.databinding.FragmentCustomDialogBinding
import com.example.weatherforecast.utils.Constants
import com.example.weatherforecast.utils.YourPreferences
import com.example.weatherforecast.utils.initSharedPref

class InitialCustomDialogFragment : DialogFragment() {
    lateinit var binding: FragmentCustomDialogBinding
    private lateinit var yourPrefrence: YourPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.fav_background)
        isCancelable = false
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okBtn.setOnClickListener{
            if(binding.radioButtonGPS.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.LOCATION,"1")
            }else if(binding.radioButtonMaps.isChecked){
                yourPrefrence = YourPreferences.getInstance(requireContext())
                yourPrefrence.saveData(Constants.LOCATION,"2")
            }
            if(activity is DialogInterface.OnDismissListener){
                (activity as (DialogInterface.OnDismissListener)).onDismiss(dialog)
            }
            dialog!!.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
    }
}