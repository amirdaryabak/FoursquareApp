package com.amirdaryabak.foursquareapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "venues"
)
data class Venue(
    @PrimaryKey
    val id: String,
//    val location: Location,
    val name: String,
    val canonicalUrl: String
) : Serializable