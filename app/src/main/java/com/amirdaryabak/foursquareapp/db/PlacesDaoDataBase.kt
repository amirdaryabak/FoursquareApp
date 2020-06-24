package com.amirdaryabak.foursquareapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amirdaryabak.foursquareapp.models.Venue

@Database(
    entities = [Venue::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PlacesDaoDataBase : RoomDatabase(){

    abstract fun getPlacesDao(): PlacesDao

    companion object {
        @Volatile
        private var instance: PlacesDaoDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDataBase(context).also { instance = it }
        }

        private fun createDataBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PlacesDaoDataBase::class.java,
                "places_db.db"
            ).build()
    }
}