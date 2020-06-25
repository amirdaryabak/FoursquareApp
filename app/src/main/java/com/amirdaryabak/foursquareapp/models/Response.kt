package com.amirdaryabak.foursquareapp.models

import com.google.gson.annotations.SerializedName

data class Response(
    val groups: List<Group>,
    val totalResults: Int,
    @SerializedName("venue")
    val venue: VenueForDetail
)