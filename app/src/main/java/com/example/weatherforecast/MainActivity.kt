package com.example.weatherforecast

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.ui.dialog.InitialCustomDialogFragment
import com.example.weatherforecast.utils.checkForInternet
import com.example.weatherforecast.utils.getLang
import com.example.weatherforecast.utils.initLANG
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true)
        if (isFirstRun) {
            when (Locale.getDefault().language.toString()) {
                "en" -> {
                    initLANG("en", this)
                }
                "ar" -> {
                    initLANG("ar", this)

                }
            }
                InitialCustomDialogFragment().show(supportFragmentManager, "MyCustomFragment")
                 getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .edit()
                .putBoolean("isFirstRun", false)
                .apply()
       }



        setSupportActionBar(binding.appBarHome2.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_settings, R.id.nav_alert
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_activity2, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(getLang(this))
        Locale.setDefault(Locale(getLang(this)))

        configuration.setLayoutDirection(Locale(getLang(this)))
        // update configuration
        resources.updateConfiguration(configuration, metric)
        // notify configuration
        onConfigurationChanged(configuration)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDismiss(p0: DialogInterface?) {
        finish()
        startActivity(intent)
    }

}