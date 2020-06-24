package com.amirdaryabak.foursquareapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amirdaryabak.foursquareapp.MainApplication
import com.amirdaryabak.foursquareapp.models.MainResponse
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
    var venuesResponse: MainResponse? = null

    fun getBreakingNews(latitudeAndLongitude: String) = viewModelScope.launch {
        getVenuesByLatAndLng(latitudeAndLongitude)
    }

    private suspend fun getVenuesByLatAndLng(latitudeAndLongitude: String) {
        venues.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = mainRepository.getVenuesByLatAndLng(latitudeAndLongitude)
                venues.postValue(handleVenuesByLatAndLngResponse(response))
            } else {
                venues.postValue((Resource.Error("Not internet connection")))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> venues.postValue((Resource.Error("Network Failure")))
                else -> venues.postValue(Resource.Error("Connection Error"))
            }
        }
    }


    private fun handleVenuesByLatAndLngResponse(response: Response<MainResponse>) : Resource<MainResponse> {
        if (response.isSuccessful) {
            response.body()?.let {resultResponse ->
                venuesResponse = resultResponse
                return Resource.Success(venuesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MainApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
        return false
    }
}