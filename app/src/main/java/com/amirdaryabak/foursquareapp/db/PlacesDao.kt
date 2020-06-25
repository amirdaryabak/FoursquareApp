package com.amirdaryabak.foursquareapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amirdaryabak.foursquareapp.models.Venue

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenue(venue: Venue): Long

    @Query("SELECT * FROM venues")
    fun getAllVenues(): LiveData<List<Venue>>

    @Query("DELETE FROM venues")
    suspend fun deleteAllVenues()
}