package com.amirdaryabak.foursquareapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amirdaryabak.foursquareapp.models.Item
import com.amirdaryabak.foursquareapp.models.Venue

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenue(venue: Venue): Long

    @Query("SELECT * FROM venues")
    fun getAllVenues(): LiveData<List<Venue>>
}