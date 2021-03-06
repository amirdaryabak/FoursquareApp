package com.amirdaryabak.foursquareapp.repository

import com.amirdaryabak.foursquareapp.api.RetrofitInstance
import com.amirdaryabak.foursquareapp.db.PlacesDaoDataBase
import com.amirdaryabak.foursquareapp.models.Venue

class MainRepository(
    val db: PlacesDaoDataBase
) {

    // api
    suspend fun getVenuesByLatAndLng(latitudeAndLongitude: String) =
        RetrofitInstance.api.getVenuesByLatAndLng(latitudeAndLongitude)

    suspend fun getVenuesDetailById(venueID: String) =
        RetrofitInstance.api.getVenuesDetailById(venueID)


    // db
    suspend fun insertVenue(venue: Venue) = db.getPlacesDao().insertVenue(venue)

    fun getAllVenues() = db.getPlacesDao().getAllVenues()

    suspend fun deleteAllVenues() = db.getPlacesDao().deleteAllVenues()

}