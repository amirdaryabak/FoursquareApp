package com.amirdaryabak.foursquareapp.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amirdaryabak.foursquareapp.R
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.ui.viewmodels.VenueDetailViewModel
import com.amirdaryabak.foursquareapp.util.Resource
import com.amirdaryabak.foursquareapp.util.showLoading
import com.androiddevs.mvvmnewsapp.ui.VenueDetailProviderFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_venue_detail.*

class VenueDetailActivity : AppCompatActivity() {

    lateinit var viewModel: VenueDetailViewModel
    lateinit var loading: Dialog

    private val TAG = "VenueDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_detail)

        loading = showLoading(this)

        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = VenueDetailProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(VenueDetailViewModel::class.java)


        viewModel.getVenuesDetailById(intent.getStringExtra("id"))
        Log.d(TAG, "Venues2 : ${intent.getStringExtra("id")}")

        viewModel.venue.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    loading.dismiss()
                    response.data?.let { response ->
                        Toasty.success(this, "Yeah").show()
                        Log.d(TAG, "Venues2 : ${response.response.venue.canonicalUrl}")
                        Log.d(TAG, "Venues22 : ${response.response.venue.contact.contact}")

                        place_name.text = response.response.venue.name
                        place_address.text = response.response.venue.location.address ?: "(empty)"
                        place_contact.text = response.response.venue.contact.contact ?: "(empty)"
                        place_instagram.text = response.response.venue.contact.instagram ?: "(empty)"

                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toasty.error(this, "Need Internet connection").show()
                    response.message?.let { message ->
                        Log.e(TAG, "Error : $message")
                    }
                }
                is Resource.Loading -> {
                    Toasty.normal(this, "Loading").show()
                    loading.show()
                }
            }
        })

    }
}