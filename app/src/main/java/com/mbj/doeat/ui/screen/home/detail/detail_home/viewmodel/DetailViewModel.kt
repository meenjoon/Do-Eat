package com.mbj.doeat.ui.screen.home.detail.detail_home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostRequest
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.component.webview.getUrl
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.ui.graph.Graph
import com.mbj.doeat.util.DateUtils
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

    private val _errorValidRecruitmentCount = MutableStateFlow<String>("")
    val errorValidRecruitmentCount: StateFlow<String> = _errorValidRecruitmentCount

    private val _isPostLoadingView = MutableStateFlow<Boolean>(false)
    val isPostLoadingView: StateFlow<Boolean> = _isPostLoadingView

    private val _isEnterChatRoom = MutableSharedFlow<Boolean>()
    val isEnterChatRoom: SharedFlow<Boolean> = _isEnterChatRoom.asSharedFlow()

    private val _showEnterChatRoom = MutableStateFlow<Boolean>(false)
    val showEnterChatRoom: StateFlow<Boolean> = _showEnterChatRoom

    private val _isPartyListNetworkError = MutableSharedFlow<Boolean>()
    val isPartyListNetworkError: SharedFlow<Boolean> = _isPartyListNetworkError.asSharedFlow()

    private val _showPartyListNetworkError = MutableStateFlow<Boolean>(false)
    val showPartyListNetworkError: StateFlow<Boolean> = _showPartyListNetworkError

    private val _isPartyListLoadingView = MutableStateFlow<Boolean>(false)
    val isPartyListLoadingView: StateFlow<Boolean> = _isPartyListLoadingView

    private val _isPostPartyNetworkError = MutableSharedFlow<Boolean>()
    val isPostPartyNetworkError: SharedFlow<Boolean> = _isPostPartyNetworkError.asSharedFlow()

    private val _showPostPartyNetworkError = MutableStateFlow<Boolean>(false)
    val showPostPartyNetworkError: StateFlow<Boolean> = _showPostPartyNetworkError

    private val _enterRoomErrorMessage = MutableStateFlow<String>("")
    val enterRoomErrorMessage: StateFlow<String> = _enterRoomErrorMessage

    private val _isEnterRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isEnterRoomLoadingView: StateFlow<Boolean> = _isEnterRoomLoadingView

    val userId = UserDataStore.getLoginResponse()?.userId

    private var chatRoomsAllEventListener: ValueEventListener? = null

    init {
        getPartiesByLocation()
        addChatRoomsAllEventListener()
    }

    fun updateSearchItem(inputSearchItem: SearchItem) {
        _searchItem.value = inputSearchItem
    }

    private fun getPartiesByLocation() {
        viewModelScope.launch {
            setPartyListLoadingState(true)
            searchItem.collectLatest { searchItem ->
                if (searchItem != null) {
                    defaultDBRepository.getPartiesByLocation(
                        searchItem.roadAddress,
                        onComplete = {
                            setPartyListLoadingState(false)
                        },
                        onError = {
                            togglePartyListNetworkErrorToggle()
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

    private fun addChatRoomsAllEventListener() {
        chatRoomsAllEventListener =
            chatDBRepository.addChatRoomsAllEventListener(
                onComplete = { },
                onError = { }
            ) { chatRoomList ->
                _chatRoomItemList.value = chatRoomList
            }
    }

    private fun removeChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(chatRoomsAllEventListener)
        }
    }

    fun postParty(navHostController: NavHostController) {
        viewModelScope.launch {
            if (recruitmentCount.value == "") {
                toggleValidToggleRecruitmentCountState("모집인원을 입력해주세요.")
            } else if (recruitmentCount.value.toInt() <= 1) {
                toggleValidToggleRecruitmentCountState("모집인원을 2명 이상 입력해주세요.")
            } else if (recruitmentCount.value.toInt() > 15) {
                toggleValidToggleRecruitmentCountState("모집인원은 15명까지 모집 가능합니다.")
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
                        setPostLoadingState(false)
                    },
                    onError = {
                        togglePostPartyNetworkErrorToggle()
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

    fun enterChatRoom(
        party: Party,
        chatRoomItemList: List<ChatRoom>?,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            setEnterRoomLoadingState(true)
            val myUserInfo = UserDataStore.getLoginResponse()
            val chatRoom = chatRoomItemList?.find { it.postId == party.postId.toString() }

            val isChatRoomFull = chatRoom?.members?.size == party.recruitmentLimit
            val isUserInChatRoom = chatRoom?.members?.any { it.value.userId == myUserInfo?.userId.toString() }

            if (isUserInChatRoom == true) {
                setEnterRoomLoadingState(false)
                NavigationUtils.navigate(
                    navController, DetailScreen.ChatDetail.navigateWithArg(
                        party.postId.toString()
                    )
                )
            } else if (!isChatRoomFull) {
                chatDBRepository.enterChatRoom(
                    onComplete = {
                        setEnterRoomLoadingState(false)
                    },
                    onError = {
                        toggleEnterChatRoomStateToggle("네트워크 연결을 다시 확인해주세요")
                    },
                    postId = party.postId.toString(),
                    postUserId = party.userId.toString(),
                    myUserId = myUserInfo?.userId.toString(),
                    restaurantName = party.restaurantName,
                    createdChatRoom = DateUtils.getCurrentTime()
                ).collectLatest { response ->
                    if (response is ApiResultSuccess) {
                        NavigationUtils.navigate(
                            navController, DetailScreen.ChatDetail.navigateWithArg(
                                party.postId.toString()
                            )
                        )
                    }
                }
            } else if (isChatRoomFull) {
                setEnterRoomLoadingState(false)
                toggleEnterChatRoomStateToggle("현재 인원이 꽉 찼습니다.")
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

    private fun toggleValidToggleRecruitmentCountState(errorMessage: String) {
        viewModelScope.launch {
            _showCreatePartyDialog.value = false
            _isValidRecruitmentCount.emit(true)
            _showValidRecruitmentCount.value = !_showValidRecruitmentCount.value
            _errorValidRecruitmentCount.value = errorMessage
        }
    }

    private fun setPostLoadingState(isLoading: Boolean) {
        _isPostLoadingView.value = isLoading
    }

    private fun toggleEnterChatRoomStateToggle(errorMessage: String) {
        viewModelScope.launch {
            _isEnterChatRoom.emit(true)
            _showEnterChatRoom.value = !showEnterChatRoom.value
            _enterRoomErrorMessage.value = errorMessage
        }
    }

    fun validateRecruitmentCount(newValue: String): Boolean {
        return newValue.isEmpty() || newValue.toInt() <= 1 || newValue.toInt() > 15
    }

    private fun togglePartyListNetworkErrorToggle() {
        viewModelScope.launch {
            _isPartyListNetworkError.emit(true)
            _showPartyListNetworkError.value = !showPartyListNetworkError.value
        }
    }

    private fun setPartyListLoadingState(isLoading: Boolean) {
        _isPartyListLoadingView.value = isLoading
    }

    private fun togglePostPartyNetworkErrorToggle() {
        viewModelScope.launch {
            _isPostPartyNetworkError.emit(true)
            _showPostPartyNetworkError.value = !showPostPartyNetworkError.value
            _showCreatePartyDialog.value = false
        }
    }

    private fun setEnterRoomLoadingState(isLoading: Boolean) {
        _isEnterRoomLoadingView.value = isLoading
    }

    override fun onCleared() {
        super.onCleared()
        removeChatRoomsAllEventListener()
    }
}
