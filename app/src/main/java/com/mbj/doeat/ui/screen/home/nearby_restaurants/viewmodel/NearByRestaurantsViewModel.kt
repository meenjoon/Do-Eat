package com.mbj.doeat.ui.screen.home.nearby_restaurants.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.SearchResult
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.famous_restarant.repository.FamousRestaurantRepository
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

    private val _showLocationPermissionDeniedToast = MutableStateFlow<Boolean>(false)
    val showLocationPermissionDeniedToast: StateFlow<Boolean> = _showLocationPermissionDeniedToast

    private val _searchResult = MutableSharedFlow<SearchResult>()
    val searchResult: SharedFlow<SearchResult> = _searchResult.asSharedFlow()

    private val _searchWidgetState = MutableStateFlow(SearchWidgetState.CLOSED)
    val searchWidgetState: StateFlow<SearchWidgetState> = _searchWidgetState

    private val _searchTextState = MutableStateFlow("")
    val searchTextState: StateFlow<String> = _searchTextState

    private val _isSearchInvalid = MutableSharedFlow<Boolean>()
    val isSearchInvalid: SharedFlow<Boolean> = _isSearchInvalid.asSharedFlow()

    private val _showSearchInvalidToast = MutableStateFlow<Boolean>(false)
    val showSearchInvalidToast: StateFlow<Boolean> = _showSearchInvalidToast

    private val _searchResultCollapse = MutableSharedFlow<Boolean>()
    val searchResultCollapse: SharedFlow<Boolean> = _searchResultCollapse

    private val _showSearchResultCollapse = MutableStateFlow<Boolean>(false)
    val showSearchResultCollapse: StateFlow<Boolean> = _showSearchResultCollapse

    init {
        viewModelScope.launch {
            searchResult.collectLatest { searchResult ->
                handleSearchResult(searchResult)
            }
        }
    }

    fun updateLocation(newValue: LatLng) {
        _location.value = newValue
    }

    fun setLocationPermissionDenied() {
        viewModelScope.launch {
            _isLocationPermissionDenied.emit(true)
            _showLocationPermissionDeniedToast.value = !_showLocationPermissionDeniedToast.value
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

    fun updateSearchText(newValue: String) {
        _searchTextState.value = newValue
    }

    private fun handleSearchResult(searchResult: SearchResult) {
        viewModelScope.launch {
            if (searchResult.items.isEmpty()) {
                _isSearchInvalid.emit(true)
                _showSearchInvalidToast.value = !_showSearchInvalidToast.value
            }
        }
    }

    fun toggleSearchResultCollapsed() {
        viewModelScope.launch {
            _searchResultCollapse.emit(true)
            _showSearchResultCollapse.value = !_showSearchResultCollapse.value
        }
    }
}
