package com.example.phase3


import CreateFragment
import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import sun.jvm.hotspot.utilities.IntArray





class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_ACCESS_LOCATION: Int = 101
    private var latitude: Double = 37.4216327
    private var longitude: Double = -122.0839757
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_subscribe -> {
                    println("subscribe pressed")
                    replaceFragment(SubscribeFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_discover -> {
                    println("discover pressed")
                    replaceFragment(DiscoverFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_create -> {
                    println("create pressed")
                    replaceFragment(CreateFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_manage -> {
                    println("manage pressed")
                    replaceFragment(ManageFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        saveInfo(applicationContext,"location", "${latitute},${longitute}")
        saveInfo(applicationContext, "latitude", latitude.toString())
        saveInfo(applicationContext, "longitude", longitude.toString())

        getLocation()
        bottomNavigationView.setOnNavigationItemSelectedListener(
            onNavigationItemSelectedListener
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun getLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                latitude = location!!.latitude
                longitude = location!!.longitude
//                saveInfo(applicationContext,"location", "${latitute},${longitute}")
                saveInfo(applicationContext, "longitude", longitude.toString())
                saveInfo(applicationContext, "latitude", latitude.toString())
//                shareEditor = sharedPref.edit()
//                shareEditor.putString("latitute", latitute.toString())
//                shareEditor.putString("longitute", longitute.toString())
//                shareEditor.apply()
                Log.d("LocationInfo", "Latitute: $latitude ; Longitute: $longitude")
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }
        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            Toast.makeText(applicationContext, "Fehler bei der Erfassung!", Toast.LENGTH_SHORT)
                .show()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_ACCESS_LOCATION
            )
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
            }
        }
    }
}
