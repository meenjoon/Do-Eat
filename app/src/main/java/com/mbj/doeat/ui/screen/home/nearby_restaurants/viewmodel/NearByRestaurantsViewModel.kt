package com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NearByRestaurantsViewModel: ViewModel() {

    private val _location = MutableStateFlow<LatLng>(LatLng(37.532600, 127.024612))
    val location: StateFlow<LatLng> = _location

    private val _isLocationPermissionDenied = MutableSharedFlow<Boolean>()
    val isLocationPermissionDenied: SharedFlow<Boolean> = _isLocationPermissionDenied.asSharedFlow()

    private val _isLocationPermissionDeniedCount = MutableStateFlow<Int>(0)
    val isLocationPermissionDeniedCount: StateFlow<Int> = _isLocationPermissionDeniedCount

    fun updateLocation(latLng: LatLng) {
        _location.value = latLng
    }

    fun updateLocationPermissionDenied() {
        viewModelScope.launch {
            _isLocationPermissionDenied.emit(true)
            _isLocationPermissionDeniedCount.value = _isLocationPermissionDeniedCount.value + 1
        }
    }
}
