package com.amirdaryabak.foursquareapp.models

data class Location(
    val address: String,
    val cc: String,
    val city: String,
    val country: String,
    val crossStreet: String,
    val distance: Int,
    val formattedAddress: List<String>,
//    val labeledLatLngs: List<LabeledLatLng>,
    val lat: Double,
    val lng: Double,
    val postalCode: String,
    val state: String
)