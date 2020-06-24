package com.amirdaryabak.foursquareapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.amirdaryabak.foursquareapp.util.Constants.Companion.CLIENT_ID
import com.amirdaryabak.foursquareapp.util.Constants.Companion.CLIENT_SECRET
import com.foursquare.pilgrim.*

class MainApplication : Application() {



    override fun onCreate() {
        super.onCreate()

        val pilgrimNotificationHandler = object : PilgrimNotificationHandler() {
            // Primary visit handler
            override fun handleVisit(context: Context, notification: PilgrimSdkVisitNotification) {
                val visit = notification.visit
//                val venue = visit.venue
                Log.d("PilgrimSdk", visit.toString())
            }

            // Optional: If visit occurred while in Doze mode or without network connectivity
            override fun handleBackfillVisit(context: Context, notification: PilgrimSdkBackfillNotification) {
                val visit = notification.visit
//                val venue = visit.venue
                Log.d("PilgrimSdk", visit.toString())
            }

            // Optional: If visit occurred by triggering a geofence
            override fun handleGeofenceEventNotification(context: Context, notification: PilgrimSdkGeofenceEventNotification) {
                super.handleGeofenceEventNotification(context, notification)
                // Process the geofence events however you'd like. Here we loop through the potentially multiple geofence events and handle them individually:
                notification.geofenceEvents.forEach { geofenceEvent ->
                    Log.d("PilgrimSdk", geofenceEvent.toString())
                }
            }

        }

        PilgrimSdk.with(
            PilgrimSdk.Builder(this)
                .consumer(CLIENT_ID, CLIENT_SECRET)
                .notificationHandler(pilgrimNotificationHandler)
                .logLevel(LogLevel.DEBUG)
        )
    }
}