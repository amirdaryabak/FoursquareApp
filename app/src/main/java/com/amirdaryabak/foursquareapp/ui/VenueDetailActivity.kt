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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_detail)

        loading = showLoading(this)

        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = VenueDetailProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(VenueDetailViewModel::class.java)

        viewModel.getVenuesDetailById(intent.getStringExtra("id"))

        viewModel.venue.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    loading.dismiss()
                    response.data?.let { result ->
                        place_name.text = result.response.venue.name
                        place_address.text = result.response.venue.location.address ?: "(empty)"
                        place_contact.text = result.response.venue.contact.contact ?: "(empty)"
                        place_instagram.text = result.response.venue.contact.instagram ?: "(empty)"

                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toasty.error(this, getString(R.string.noInternetConnection)).show()
                }
                is Resource.Loading -> {
                    loading.show()
                }
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}