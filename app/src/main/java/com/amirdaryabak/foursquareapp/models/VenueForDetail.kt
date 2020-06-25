package com.amirdaryabak.foursquareapp.models

import java.io.Serializable


data class VenueForDetail(
    val id: String,
    val location: Location,
    val name: String,
    val canonicalUrl: String,
    val contact: Contact
) : Serializable