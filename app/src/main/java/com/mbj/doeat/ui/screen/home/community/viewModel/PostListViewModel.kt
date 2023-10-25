package com.mbj.doeat.ui.screen.home.community.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.util.DateUtils.getCurrentTime
import com.mbj.doeat.util.MapConverter
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UrlUtils
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val chatDBRepository: ChatDBRepository
) : ViewModel() {

    val userId = UserDataStore.getLoginResponse()?.userId

    val partyList = getAllPartyList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private val _searchBarText = MutableStateFlow("")
    val searchBarText: StateFlow<String> = _searchBarText

    private val _chatRoomItemList = MutableStateFlow<List<ChatRoom>?>(emptyList())
    val chatRoomItemList: StateFlow<List<ChatRoom>?> = _chatRoomItemList

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

    private val _isChatRoomListNetworkError = MutableSharedFlow<Boolean>()
    val isChatRoomListNetworkError: SharedFlow<Boolean> = _isChatRoomListNetworkError.asSharedFlow()

    private val _showChatRoomListNetworkError = MutableStateFlow<Boolean>(false)
    val showChatRoomListNetworkError: StateFlow<Boolean> = _showChatRoomListNetworkError

    private val _isChatRoomListLoadingView = MutableStateFlow<Boolean>(false)
    val isChatRoomListLoadingView: StateFlow<Boolean> = _isChatRoomListLoadingView

    private val _enterRoomErrorMessage = MutableStateFlow<String>("")
    val enterRoomErrorMessage: StateFlow<String> = _enterRoomErrorMessage

    private val _isEnterRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isEnterRoomLoadingView: StateFlow<Boolean> = _isEnterRoomLoadingView

    private var chatRoomsAllEventListener: ValueEventListener? = null

    init {
        setPartyListLoadingState(true)
        addChatRoomsAllEventListener()
    }

    private fun getAllPartyList(): Flow<List<Party>> {
        return defaultDBRepository.getAllPartyList(
            onComplete = { setPartyListLoadingState(false) },
            onError = { togglePartyListNetworkErrorToggle() }
        ).map { response ->
            when (response) {
                is ApiResultSuccess -> response.data
                else -> {
                    emptyList()
                }
            }
        }
    }

    fun updateSearchBarText(newText: String) {
        _searchBarText.value = newText
    }

    fun getFilteredPartyList(partyList: List<Party>, searchText: String): List<Party> {
        val searchTextLower = searchText.lowercase()
        return partyList.filter { party ->
            party.restaurantName.lowercase().contains(searchTextLower)
        }
    }

    private fun addChatRoomsAllEventListener() {
        viewModelScope.launch {
            setChatRoomListLoadingState(true)
            chatRoomsAllEventListener =
                chatDBRepository.addChatRoomsAllEventListener(
                    onComplete = {
                        setChatRoomListLoadingState(false)
                    },
                    onError = {
                        toggleChatRoomListNetworkErrorToggle()
                    }
                ) { chatRoomList ->
                    _chatRoomItemList.value = chatRoomList
                }
        }
    }

    private fun removeChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(chatRoomsAllEventListener)
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
            val isUserInChatRoom =
                chatRoom?.members?.any { it.value.userId == myUserInfo?.userId.toString() }

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
                    createdChatRoom = getCurrentTime()
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

    private fun toggleEnterChatRoomStateToggle(errorMessage: String) {
        viewModelScope.launch {
            _isEnterChatRoom.emit(true)
            _showEnterChatRoom.value = !showEnterChatRoom.value
            _enterRoomErrorMessage.value = errorMessage
        }
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

    private fun toggleChatRoomListNetworkErrorToggle() {
        viewModelScope.launch {
            _isChatRoomListNetworkError.emit(true)
            _showChatRoomListNetworkError.value = !showChatRoomListNetworkError.value
        }
    }

    private fun setChatRoomListLoadingState(isLoading: Boolean) {
        _isChatRoomListLoadingView.value = isLoading
    }

    private fun setEnterRoomLoadingState(isLoading: Boolean) {
        _isEnterRoomLoadingView.value = isLoading
    }

    override fun onCleared() {
        super.onCleared()
        removeChatRoomsAllEventListener()
    }
}
