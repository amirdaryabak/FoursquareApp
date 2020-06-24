package com.amirdaryabak.foursquareapp.db

import androidx.room.TypeConverter
import com.amirdaryabak.foursquareapp.models.Location

class Converters {

    @TypeConverter
    fun fromLocation(location: Location): String {
        return location.address
    }

    @TypeConverter
    fun toLocation(address: String): Location {
        return Location(address)
    }
}