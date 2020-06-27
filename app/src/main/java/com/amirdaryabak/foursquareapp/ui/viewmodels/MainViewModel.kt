package com.amirdaryabak.foursquareapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amirdaryabak.foursquareapp.models.MainResponse
import com.amirdaryabak.foursquareapp.models.Venue
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModel(
    app: Application,
    val mainRepository: MainRepository
) : AndroidViewModel(app) {

    val venues: MutableLiveData<Resource<MainResponse>> = MutableLiveData()

    // api
    fun getVenuesByLatAndLng(latitudeAndLongitude: String) = viewModelScope.launch {
        try {
            venues.postValue(Resource.Loading())
            val response = mainRepository.getVenuesByLatAndLng(latitudeAndLongitude)
            venues.postValue(handleVenuesByLatAndLngResponse(response))
        } catch (t: Throwable) {
            when(t) {
                is IOException -> venues.postValue((Resource.Error("Network Failure")))
                else -> venues.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    private fun handleVenuesByLatAndLngResponse(response: Response<MainResponse>): Resource<MainResponse> {
        if (response.isSuccessful) {
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // db
    fun insertVenue(venue: Venue) = viewModelScope.launch {
        mainRepository.insertVenue(venue)
    }

    fun getAllVenues() = mainRepository.getAllVenues()

    fun deleteAllVenues() = viewModelScope.launch {
        mainRepository.deleteAllVenues()
    }


}