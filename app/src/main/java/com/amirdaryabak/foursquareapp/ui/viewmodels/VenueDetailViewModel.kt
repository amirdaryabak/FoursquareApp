package com.amirdaryabak.foursquareapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amirdaryabak.foursquareapp.models.MainResponse
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class VenueDetailViewModel(
    app: Application,
    val mainRepository: MainRepository
) : AndroidViewModel(app) {

    val venue: MutableLiveData<Resource<MainResponse>> = MutableLiveData()

    // api
    fun getVenuesDetailById(venueID: String) = viewModelScope.launch {
        try {
            venue.postValue(Resource.Loading())
            val response = mainRepository.getVenuesDetailById(venueID)
            venue.postValue(handleVenuesDetailByIdResponse(response))
        } catch (t: Throwable) {
            when(t) {
                is IOException -> venue.postValue((Resource.Error("Network Failure")))
                else -> venue.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    private fun handleVenuesDetailByIdResponse(response: Response<MainResponse>) : Resource<MainResponse> {
        if (response.isSuccessful) {
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}