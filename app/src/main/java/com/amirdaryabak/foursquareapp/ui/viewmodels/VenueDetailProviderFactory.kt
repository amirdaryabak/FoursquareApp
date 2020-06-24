package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.ui.viewmodels.MainViewModel
import com.amirdaryabak.foursquareapp.ui.viewmodels.VenueDetailViewModel

class VenueDetailProviderFactory(
    val app: Application,
    val mainRepository: MainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VenueDetailViewModel(
            app,
            mainRepository
        ) as T
    }
}