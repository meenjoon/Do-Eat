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

    private val _isSearchInvalid = MutableSharedFlow<Boolean>()
    val isSearchInvalid: SharedFlow<Boolean> = _isSearchInvalid.asSharedFlow()

    private val _isSearchInvalidCount = MutableStateFlow<Int>(0)
    val isSearchInvalidCount: StateFlow<Int> = _isSearchInvalidCount

    private val _searchResultCollapse = MutableSharedFlow<Boolean>()
    val searchResultCollapse: SharedFlow<Boolean> = _searchResultCollapse

    private val _searchResultCollapseCount = MutableStateFlow<Int>(0)
    val searchResultCollapseCount: StateFlow<Int> = _searchResultCollapseCount

    init {
        viewModelScope.launch {
            searchResult.collectLatest { searchResult ->
                updateSearchInvalid(searchResult)
            }
        }
    }

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

    private fun updateSearchInvalid(searchResult: SearchResult) {
        viewModelScope.launch {
            if (searchResult.items.isEmpty()) {
                _isSearchInvalid.emit(true)
                _isSearchInvalidCount.value = _isSearchInvalidCount.value + 1
            }
        }
    }

    fun toggleSearchResultCollapse() {
        viewModelScope.launch {
            _searchResultCollapse.emit(true)
            _searchResultCollapseCount.value = _searchResultCollapseCount.value + 1
        }
    }
}
