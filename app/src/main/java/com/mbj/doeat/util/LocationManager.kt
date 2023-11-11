package com.mbj.doeat.util

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng

class LocationManager(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val onLocationUpdate: (LatLng) -> Unit
) {
    private var locationCallback: LocationCallback? = null

    fun startLocationUpdates() {
        try {
            val locationRequest = buildLocationRequest()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val lastLocation = locationResult.lastLocation
                    lastLocation?.let { latLng ->
                        onLocationUpdate(LatLng(latLng.latitude, latLng.longitude))
                    }
                    stopLocationUpdates()
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback as LocationCallback, Looper.getMainLooper())

        } catch (e: SecurityException) {
            Log.d("LocationManager", e.stackTraceToString())
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    private fun buildLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5 * 1000L).apply {
            setMinUpdateDistanceMeters(0.0f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }
}

