package com.amirdaryabak.foursquareapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "venues"
)
data class Venue(
    @PrimaryKey
    val id: String,
    @Embedded(prefix = "location")
    val location: Location,
    val name: String
) : Serializable