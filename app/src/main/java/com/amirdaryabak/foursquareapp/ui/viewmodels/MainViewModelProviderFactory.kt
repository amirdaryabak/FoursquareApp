package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirdaryabak.foursquareapp.repository.MainRepository
import com.amirdaryabak.foursquareapp.ui.viewmodels.MainViewModel

class MainViewModelProviderFactory(
    val app: Application,
    val mainRepository: MainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(
            app,
            mainRepository
        ) as T
    }
}