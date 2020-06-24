package com.amirdaryabak.foursquareapp.api

import com.amirdaryabak.foursquareapp.models.MainResponse
import com.amirdaryabak.foursquareapp.util.Constants.Companion.CLIENT_ID
import com.amirdaryabak.foursquareapp.util.Constants.Companion.CLIENT_SECRET
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {

    @GET("venues/explore")
    suspend fun getVenuesByLatAndLng(
        @Query("ll") latitudeAndLongitude: String,
        @Query("client_id") client_id: String = CLIENT_ID,
        @Query("client_id") client_secret: String = CLIENT_SECRET
    ): Response<MainResponse>

    @GET("venues/{VENUE_ID}")
    suspend fun getVenuesById(
        @Path("VENUE_ID") venueID: String
    ): Response<MainResponse>
}