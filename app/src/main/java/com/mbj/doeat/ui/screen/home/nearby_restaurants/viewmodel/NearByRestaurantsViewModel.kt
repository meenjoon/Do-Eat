package com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel

import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

class NearByRestaurantsViewModel: ViewModel() {

    private val _location = MutableStateFlow<LatLng>(LatLng(37.532600, 127.024612))
    val location: StateFlow<LatLng> = _location
//    private val _location = MutableSharedFlow<LatLng>(replay = 1)
//    val location: SharedFlow<LatLng> = _location.asSharedFlow()

//    init {
//        _location.tryEmit(LatLng(37.532600, 127.024612))
//    }

    fun updateLocation(latLng: LatLng) {
        _location.value = latLng
//        _location.tryEmit(latLng)
    }
}
