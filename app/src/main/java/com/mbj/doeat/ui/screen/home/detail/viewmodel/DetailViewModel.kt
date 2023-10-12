package com.mbj.doeat.ui.screen.home.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val defaultDBRepository: DefaultDBRepository) : ViewModel() {

    private val _partyList = MutableStateFlow<List<Party>>((emptyList()))
    val partyList: StateFlow<List<Party>> = _partyList

    private val _searchItem = MutableStateFlow<SearchItem?>(null)
    val searchItem: StateFlow<SearchItem?> = _searchItem

    private val _recruitmentCount = MutableStateFlow("")
    val recruitmentCount: StateFlow<String> = _recruitmentCount

    private val _recruitmentDetails = MutableStateFlow("")
    val recruitmentDetails: StateFlow<String> = _recruitmentDetails

    init {
        viewModelScope.launch {
            searchItem.collectLatest { searchItem ->
                if (searchItem != null) {
                    getPartiesByLocation(restaurantLocation = searchItem.roadAddress)
                }
            }
        }
    }

    fun updateSearchItem(inputSearchItem: SearchItem) {
        _searchItem.value = inputSearchItem
    }

    private suspend fun getPartiesByLocation(restaurantLocation: String) {
        defaultDBRepository.getPartiesByLocation(
            restaurantLocation,
            onComplete = {
            },
            onError = {
            }
        ).collectLatest { responsePartyList ->
            if (responsePartyList is ApiResultSuccess) {
                _partyList.value = responsePartyList.data
            }
        }
    }

    fun changeRecruitmentCount(recruitCount: String) {
        _recruitmentCount.value = recruitCount
    }

    fun changeRecruitmentDetails(recruitDetails: String) {
        _recruitmentDetails.value = recruitDetails
    }
}
