package com.example.vibees.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import android.location.Address
import java.util.Locale

class Geolocation(private val activityResultRegistry: ActivityResultRegistry) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun locationPermissionRequest(activity: Activity, callback: (Double, Double) -> Unit): ActivityResultLauncher<Array<String>> {
        return activityResultRegistry.register(
            "key_location_permission_request",
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted. You can proceed with retrieving the location.
                    getLocation(activity, callback)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted. You can proceed with retrieving the location.
                    getLocation(activity, callback)
                }
                else -> {
                    // No location access granted. Handle the case where permission is denied.
                    // You can show a message or take appropriate action.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(activity: Activity, callback: (Double, Double) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Handle the retrieved location, similar to your previous code.
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d(ContentValues.TAG, "LOCATION RETRIEVED")
                Log.d(ContentValues.TAG, "Longitude: $longitude")
                Log.d(ContentValues.TAG, "Latitude: $latitude")
                callback(latitude, longitude)
            }
        }
    }

    fun getUserLocation(context: Context, latitude: Double, longitude: Double): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
        val address: Address = addresses[0]
        return address
    }
}