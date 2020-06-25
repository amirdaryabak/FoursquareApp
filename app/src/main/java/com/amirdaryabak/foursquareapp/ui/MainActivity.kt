package com.amirdaryabak.foursquareapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.androiddevs.mvvmnewsapp.ui.MainViewModelProviderFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var viewModel: MainViewModel
    lateinit var newsAdapter: PlacesAdapter
    var venuesArrayList: MutableList<Venue> = ArrayList()

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRepository = MainRepository(PlacesDaoDataBase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        val savedLatitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("latitude","0")
        val savedLongitude = getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("longitude","0")

        if (intent.getBooleanExtra("needToRefresh", true)) {
            viewModel.getSafeVenuesByLatAndLng("$savedLatitude,$savedLongitude")
        } else {
            /*viewModel.getAllVenues().observe(this, Observer {
                setUpRecyclerView(it)
            })*/
        }



        viewModel.venues.observe(this, Observer {response ->
            when (response) {
                is Resource.Success -> {
//                    loading.dismiss()
                    response.data?.let { response ->
                        Toasty.success(this, "Yeah").show()
                        Log.d(TAG, "TotalResult : ${response.response.totalResults}")
                        for (i in response.response.groups) {
                            for (j in i.items) {
                                venuesArrayList.add(j.venue)
                                val venue = Venue(j.venue.id,j.venue.name,"url")
                                viewModel.insertVenue(venue)
                                Log.d(TAG, "Venues : ${j.venue.id}")
                            }
                        }
                        setUpRecyclerView(venuesArrayList)
                    }
                }
                is Resource.Error -> {
//                    loading.dismiss()
                    Toasty.error(this, "Yeah").show()
                    /*viewModel.getAllVenues().observe(this, Observer {
                        setUpRecyclerView(it)
                    })*/
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

    private fun setUpRecyclerView(venueList: List<Venue>) {
        newsAdapter = PlacesAdapter()
        newsAdapter.setOnItemClickListener {item->
            intent = Intent(this,VenueDetailActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
        }
        newsAdapter.differ.submitList(venueList)
        rvPlaces.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

}