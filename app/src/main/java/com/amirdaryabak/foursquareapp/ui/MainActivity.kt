package com.amirdaryabak.foursquareapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirdaryabak.foursquareapp.R
import com.amirdaryabak.foursquareapp.adapters.PlacesAdapter
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase
import com.amirdaryabak.foursquareapp.models.Venue
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.ui.viewmodels.MainViewModel
import com.amirdaryabak.foursquareapp.util.Resource
import com.amirdaryabak.foursquareapp.util.showLoading
import com.androiddevs.mvvmnewsapp.ui.MainViewModelProviderFactory
import com.google.android.gms.location.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {


    lateinit var viewModel: MainViewModel
    lateinit var newsAdapter: PlacesAdapter
    var venuesArrayList: MutableList<Venue> = ArrayList()
    lateinit var loading: Dialog
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loading = showLoading(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        initViewModel()
        initVenuesObserver()

    }

    override fun onStop() {
        super.onStop()
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    private fun initVenuesObserver() {
        viewModel.venues.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    loading.dismiss()
                    response.data?.let { result ->
                        viewModel.deleteAllVenues()
                        for (i in result.response.groups) {
                            for (j in i.items) {
                                venuesArrayList.add(j.venue)
                                viewModel.insertVenue(j.venue)
                            }
                        }
                        setUpRecyclerView(venuesArrayList)
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    viewModel.getAllVenues().observe(this, Observer {
                        if (it.isNotEmpty()) {
                            setUpRecyclerView(it)
                        } else {
                            Toasty.error(this, getString(R.string.noInternet)).show()
                            onFailure_tv.visibility = View.VISIBLE
                        }
                    })
                }
                is Resource.Loading -> {
                    loading.show()
                }
            }
        })
    }

    private fun initViewModel() {
        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
    }

    private fun setUpRecyclerView(venueList: List<Venue>) {
        newsAdapter = PlacesAdapter()
        newsAdapter.setOnItemClickListener { item ->
            intent = Intent(this, VenueDetailActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        newsAdapter.differ.submitList(venueList)
        rvPlaces.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) {
                    requestNewLocationData()
                }
            } else {
                Toasty.error(this, "Turn on location", Toast.LENGTH_LONG).show()
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
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            val savedLatitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("latitude","0")
            val savedLongitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("longitude","0")
            if (savedLatitude != null && savedLatitude != "0" && savedLongitude != null && savedLongitude != "0") {
                if (getDistance(latitude, longitude, savedLatitude.toDouble(), savedLongitude.toDouble()) > 100) {
                    saveLatitudeAndLongitude(latitude.toString(), longitude.toString())
                    viewModel.getVenuesByLatAndLng("$latitude,$longitude")
                } else {
                    viewModel.getAllVenues().observe(this@MainActivity, Observer {
                        if (it.isNotEmpty()){
                            setUpRecyclerView(it)
                        } else {
                            viewModel.getVenuesByLatAndLng("$savedLatitude,$savedLongitude")
                        }
                    })
                }
            } else {
                saveLatitudeAndLongitude(latitude.toString(), longitude.toString())
                viewModel.getAllVenues().observe(this@MainActivity, Observer {
                    if (it.isNotEmpty()){
                        setUpRecyclerView(it)
                    } else {
                        viewModel.getVenuesByLatAndLng("$latitude,$longitude")
                    }
                })
            }

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
        val radiusOfEarth = 6371

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) + (Math.cos(
            Math.toRadians(lat1)
        ) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = radiusOfEarth.toDouble() * c * 1000.0

        distance = Math.pow(distance, 2.0)
        return sqrt(distance)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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
        builder.setMessage("You need to give location permission to enter app")
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

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
}
