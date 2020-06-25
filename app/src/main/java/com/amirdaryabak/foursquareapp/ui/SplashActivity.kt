package com.amirdaryabak.foursquareapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.amirdaryabak.foursquareapp.R
import com.google.android.gms.location.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class SplashActivity : AppCompatActivity() {

    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val TAG = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val savedLatitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("latitude","0")
                        val savedLongitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("longitude","0")
                        Log.d(TAG, savedLatitude)
                        Log.d(TAG, savedLongitude)
                        Log.d(TAG, location.latitude.toString())
                        Log.d(TAG, location.longitude.toString())
                        if (savedLatitude != null && savedLatitude != "0" && savedLongitude != null && savedLongitude != "0"){
                            if (getDistance(location.latitude,location.longitude, savedLatitude.toDouble(), savedLongitude.toDouble()) > 100) {
                                saveLatitudeAndLongitude(location.latitude.toString(), location.longitude.toString())

                                val intent = Intent(this@SplashActivity,MainActivity::class.java)
                                intent.putExtra("needToRefresh", true)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                saveLatitudeAndLongitude(location.latitude.toString(), location.longitude.toString())

                                val intent = Intent(this@SplashActivity,MainActivity::class.java)
                                intent.putExtra("needToRefresh", false)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        } else {
                            saveLatitudeAndLongitude(location.latitude.toString(), location.longitude.toString())
                            val intent = Intent(this@SplashActivity,MainActivity::class.java)
                            intent.putExtra("needToRefresh", true)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            Log.d(TAG, mLastLocation.latitude.toString())
            Log.d(TAG, mLastLocation.longitude.toString())
            val intent = Intent(this@SplashActivity,MainActivity::class.java)
            intent.putExtra("needToRefresh", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun saveLatitudeAndLongitude(latitude: String, longitude: String) {
        val shared = getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("latitude", latitude)
        editor.putString("longitude", longitude)
        editor.apply()
    }

    private fun getDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        Log.d("TAG","Distance in meter : ${(dist * 1000 * 1000)}")
        return dist * 1000 * 1000
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            } else {
                showSettingsDialog()
            }
        }
    }
    private fun showSettingsDialog() {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Permission")
        builder.setMessage("You need give location permission to continue")
        builder.setPositiveButton(
            "Go to setting"
        ) { dialog: DialogInterface, which: Int ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Deny"
        ) { dialog: DialogInterface, which: Int ->
            dialog.cancel()
            finish()
        }
        builder.show()
    }
    // navigating userDataModel to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


}