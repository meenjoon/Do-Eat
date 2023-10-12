package com.mbj.doeat.ui.screen.home.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.Graph
import com.mbj.doeat.ui.screen.home.detail.getUrl
import com.mbj.doeat.util.UserDataStore
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

    private val _isBottomSheetExpanded = MutableStateFlow<Boolean>(false)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded

    private val _showCreatePartyDialog = MutableStateFlow<Boolean>(false)
    val showCreatePartyDialog: StateFlow<Boolean> = _showCreatePartyDialog

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

    fun postParty(navHostController: NavHostController) {
        viewModelScope.launch {
            defaultDBRepository.postParty(
                PartyPostRequest(
                    userId = UserDataStore.getLoginResponse()?.userId!!,
                    restaurantName = searchItem.value?.title ?: "",
                    category = searchItem.value?.category ?: "",
                    restaurantLocation = searchItem.value?.roadAddress ?: "",
                    recruitmentLimit = recruitmentCount.value.toInt(),
                    currentNumberPeople = 1,
                    detail = recruitmentDetails.value,
                    link = getUrl(searchItem.value)
                ),
                onComplete = {
                },
                onError = {
                }
            ).collectLatest { party ->
                if (party is ApiResultSuccess) {
                    navHostController.navigate(Graph.HOME) {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    fun changeRecruitmentCount(recruitCount: String) {
        _recruitmentCount.value = recruitCount
    }

    fun changeRecruitmentDetails(recruitDetails: String) {
        _recruitmentDetails.value = recruitDetails
    }

    fun toggleBottomSheetState() {
        _isBottomSheetExpanded.value = !_isBottomSheetExpanded.value
    }

    fun changeShowCreatePartyDialog(showDialog: Boolean) {
        _showCreatePartyDialog.value = showDialog
    }
}
