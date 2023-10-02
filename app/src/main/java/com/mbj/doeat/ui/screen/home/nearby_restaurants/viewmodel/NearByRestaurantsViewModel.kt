package com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.repository.FamousRestaurantRepository
import com.mbj.doeat.ui.model.SearchWidgetState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearByRestaurantsViewModel @Inject constructor(
    private val famousRestaurantRepository: FamousRestaurantRepository
) : ViewModel() {

    private val _location = MutableStateFlow<LatLng>(LatLng(37.532600, 127.024612))
    val location: StateFlow<LatLng> = _location

    private val _isLocationPermissionDenied = MutableSharedFlow<Boolean>()
    val isLocationPermissionDenied: SharedFlow<Boolean> = _isLocationPermissionDenied.asSharedFlow()

    private val _isLocationPermissionDeniedCount = MutableStateFlow<Int>(0)
    val isLocationPermissionDeniedCount: StateFlow<Int> = _isLocationPermissionDeniedCount

    private val _searchResult = MutableSharedFlow<SearchResult>()
    val searchResult: SharedFlow<SearchResult> = _searchResult.asSharedFlow()

    private val _searchWidgetState = MutableStateFlow(SearchWidgetState.CLOSED)
    val searchWidgetState: StateFlow<SearchWidgetState> = _searchWidgetState

    private val _searchTextState = MutableStateFlow("")
    val searchTextState: StateFlow<String> = _searchTextState

    fun updateLocation(newValue: LatLng) {
        _location.value = newValue
    }

    fun updateLocationPermissionDenied() {
        viewModelScope.launch {
            _isLocationPermissionDenied.emit(true)
            _isLocationPermissionDeniedCount.value = _isLocationPermissionDeniedCount.value + 1
        }
    }

    fun getFamousRestaurant(query: String) {
        viewModelScope.launch {
            famousRestaurantRepository.getSearchResult(query,
                onComplete = {
                },
                onError = {
                }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    _searchResult.emit(response.data)
                }
            }
        }
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }
}
