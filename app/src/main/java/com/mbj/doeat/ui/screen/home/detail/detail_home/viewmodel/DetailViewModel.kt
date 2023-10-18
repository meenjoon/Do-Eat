package com.mbj.doeat.ui.screen.home.detail.detail_home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.component.getUrl
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.ui.graph.Graph
import com.mbj.doeat.util.MapConverter
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UrlUtils
import com.mbj.doeat.util.UserDataStore
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
class DetailViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val chatDBRepository: ChatDBRepository
) : ViewModel() {

    private val _partyList = MutableStateFlow<List<Party>>((emptyList()))
    val partyList: StateFlow<List<Party>> = _partyList

    private val _searchItem = MutableStateFlow<SearchItem?>(null)
    val searchItem: StateFlow<SearchItem?> = _searchItem

    private val _chatRoomItemList = MutableStateFlow<List<ChatRoom>?>(emptyList())
    val chatRoomItemList: StateFlow<List<ChatRoom>?> = _chatRoomItemList

    private val _recruitmentCount = MutableStateFlow("")
    val recruitmentCount: StateFlow<String> = _recruitmentCount

    private val _recruitmentDetails = MutableStateFlow("")
    val recruitmentDetails: StateFlow<String> = _recruitmentDetails

    private val _isBottomSheetExpanded = MutableStateFlow<Boolean>(false)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded

    private val _showCreatePartyDialog = MutableStateFlow<Boolean>(false)
    val showCreatePartyDialog: StateFlow<Boolean> = _showCreatePartyDialog

    private val _isValidRecruitmentCount = MutableSharedFlow<Boolean>()
    val isValidRecruitmentCount: SharedFlow<Boolean> = _isValidRecruitmentCount.asSharedFlow()

    private val _showValidRecruitmentCount = MutableStateFlow<Boolean>(false)
    val showValidRecruitmentCount: StateFlow<Boolean> = _showValidRecruitmentCount

    private val _isPostLoadingView = MutableStateFlow<Boolean>(false)
    val isPostLoadingView: StateFlow<Boolean> = _isPostLoadingView

    val userId = UserDataStore.getLoginResponse()?.userId

    init {
        getPartiesByLocation()
        getAllChatRoomItem()
    }

    fun updateSearchItem(inputSearchItem: SearchItem) {
        _searchItem.value = inputSearchItem
    }

    private fun getPartiesByLocation() {
        viewModelScope.launch {
            searchItem.collectLatest { searchItem ->
                if (searchItem != null) {
                    defaultDBRepository.getPartiesByLocation(
                        searchItem.roadAddress,
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
            }
        }
    }

    private fun getAllChatRoomItem() {
        viewModelScope.launch {
            chatDBRepository.getAllChatRoomItem(
                onComplete = { },
                onError = { }
            ) { chatRoomItemList ->
                _chatRoomItemList.value = chatRoomItemList
            }
        }
    }

    fun postParty(navHostController: NavHostController) {
        viewModelScope.launch {
            if (recruitmentCount.value == "") {
                _showCreatePartyDialog.value = false
                toggleValidToggleRecruitmentCountState()
            } else {
                setPostLoadingState(true)
                defaultDBRepository.postParty(
                    PartyPostRequest(
                        userId = UserDataStore.getLoginResponse()?.userId!!,
                        restaurantName = searchItem.value?.title ?: "",
                        category = searchItem.value?.category ?: "",
                        restaurantLocation = searchItem.value?.roadAddress ?: "",
                        recruitmentLimit = recruitmentCount.value.toInt(),
                        currentNumberPeople = 1,
                        detail = recruitmentDetails.value,
                        link = getUrl(searchItem.value?.link, searchItem.value?.title!!)
                    ),
                    onComplete = {
                    },
                    onError = {
                    }
                ).collectLatest { party ->
                    if (party is ApiResultSuccess) {
                        setPostLoadingState(false)
                        navHostController.navigate(Graph.HOME) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDetailInfoClick(party: Party, navController: NavHostController) {

        val encodedLink = UrlUtils.encodeUrl(party.link)
        val titleWithoutHtmlTags = MapConverter.removeHtmlTags(party.restaurantName)

        if (userId == party.userId) {
            NavigationUtils.navigate(
                navController, DetailScreen.DetailWriter.navigateWithArg(
                    party.copy(
                        restaurantName = titleWithoutHtmlTags,
                        link = encodedLink
                    )
                )
            )
        } else {
            NavigationUtils.navigate(
                navController, DetailScreen.DetailParticipant.navigateWithArg(
                    party.copy(
                        restaurantName = titleWithoutHtmlTags,
                        link = encodedLink
                    )
                )
            )
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

    private fun toggleValidToggleRecruitmentCountState() {
        viewModelScope.launch {
            _isValidRecruitmentCount.emit(true)
            _showValidRecruitmentCount.value = !_showValidRecruitmentCount.value
        }
    }

    private fun setPostLoadingState(isLoading: Boolean) {
        _isPostLoadingView.value = isLoading
    }
}
