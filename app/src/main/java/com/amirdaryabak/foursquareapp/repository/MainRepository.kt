package com.amirdaryabak.foursquareapp.repository

import com.amirdaryabak.foursquareapp.api.RetrofitInstance
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase

class MainRepository(
    val db: PlacesDaoDataBase
) {
    suspend fun getVenuesByLatAndLng(latitudeAndLongitude: String) =
        RetrofitInstance.api.getVenuesByLatAndLng(latitudeAndLongitude)

    suspend fun getVenuesDetailById(venueID: String) =
        RetrofitInstance.api.getVenuesDetailById(venueID)

}