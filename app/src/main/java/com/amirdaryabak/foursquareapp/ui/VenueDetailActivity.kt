package com.amirdaryabak.foursquareapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amirdaryabak.foursquareapp.R
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.ui.viewmodels.VenueDetailViewModel
import com.amirdaryabak.foursquareapp.util.Resource
import com.androiddevs.mvvmnewsapp.ui.MainViewModelProviderFactory
import com.androiddevs.mvvmnewsapp.ui.VenueDetailProviderFactory
import es.dmoral.toasty.Toasty

class VenueDetailActivity : AppCompatActivity() {

    lateinit var viewModel: VenueDetailViewModel

    private val TAG = "VenueDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_detail)

        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = VenueDetailProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(VenueDetailViewModel::class.java)


        viewModel.getVenuesDetailById(intent.getStringExtra("id"))

        viewModel.venue.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
//                    loading.dismiss()
                    response.data?.let { response ->
                        Toasty.success(this, "Yeah").show()
                        Log.d(TAG, "Venues2 : ${response.response.venue.id}")



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