package com.amirdaryabak.foursquareapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amirdaryabak.foursquareapp.R
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.util.Resource
import com.androiddevs.mvvmnewsapp.ui.MainViewModelProviderFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var viewModel: MainViewModel

    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
        }


        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        viewModel.getBreakingNews("40.742185,-74.047285")

        viewModel.venues.observe(this, Observer {response ->
            when (response) {
                is Resource.Success -> {
//                    loading.dismiss()
                    response.data?.let { response ->
                        Toasty.success(this, "Yeah").show()
                        for (i in response.response.groups) {
                            for (j in i.items) {
                                Log.d(TAG, "Venues : ${j.venue.location.address}")
                            }
                        }
//                        loginResponse.result.id = 1
//                        viewModel.saveLogin(loginResponse.result)
//                        viewModel.getSupporterDetails(loginResponse.result.access_token)
                    }
                }
                is Resource.Error -> {
//                    loading.dismiss()
                    Toasty.error(this, "Yeah").show()
                    response.message?.let { message ->
                        Log.e(TAG, "Error : $message")
                    }
                }
                is Resource.Loading -> {
                    Toasty.normal(this, "Loading").show()
//                    loading = showLoading(this)
//                    loading.show()
                }
            }
        })
    }

}