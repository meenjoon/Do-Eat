package com.mbj.doeat.ui.screen.home.detail.detail_participant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.util.DateUtils.getCurrentTime
import com.mbj.doeat.util.NavigationUtils
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
class PartyDetailParticipantViewModel @Inject constructor(private val chatDBRepository: ChatDBRepository) : ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    private val _chatRoomItem = MutableStateFlow<ChatRoom?>(null)
    val chatRoomItem: StateFlow<ChatRoom?> = _chatRoomItem

    private val _isEnterChatRoom = MutableSharedFlow<Boolean>()
    val isEnterChatRoom: SharedFlow<Boolean> = _isEnterChatRoom.asSharedFlow()

    private val _showEnterChatRoom = MutableStateFlow<Boolean>(false)
    val showEnterChatRoom: StateFlow<Boolean> = _showEnterChatRoom

    private val _enterRoomErrorMessage = MutableStateFlow<String>("")
    val enterRoomErrorMessage: StateFlow<String> = _enterRoomErrorMessage

    private val _isEnterRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isEnterRoomLoadingView: StateFlow<Boolean> = _isEnterRoomLoadingView

    private val _isChatRoomListNetworkError = MutableSharedFlow<Boolean>()
    val isChatRoomListNetworkError: SharedFlow<Boolean> = _isChatRoomListNetworkError.asSharedFlow()

    private val _showChatRoomListNetworkError = MutableStateFlow<Boolean>(false)
    val showChatRoomListNetworkError: StateFlow<Boolean> = _showChatRoomListNetworkError

    private val _isChatRoomListLoadingView = MutableStateFlow<Boolean>(false)
    val isChatRoomListLoadingView: StateFlow<Boolean> = _isChatRoomListLoadingView

    private var observeChatRoomChangesListener: ValueEventListener? = null

    init {
        observeChatRoomChangesListener()
    }

    fun updatePartyItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }

    fun enterChatRoom(navController: NavHostController) {
        viewModelScope.launch {
            setEnterRoomLoadingState(true)
            val myUserInfo = UserDataStore.getLoginResponse()

            val isChatRoomFull = chatRoomItem.value?.members?.size == partyItem.value?.recruitmentLimit
            val isUserInChatRoom = chatRoomItem.value?.members?.any{ it.value.userId == myUserInfo?.userId.toString()}

            if (isUserInChatRoom == true) {
                setEnterRoomLoadingState(false)
                NavigationUtils.navigate(
                    navController, DetailScreen.ChatDetail.navigateWithArg(
                        partyItem.value?.postId.toString()
                    )
                )
            } else if (!isChatRoomFull){
                chatDBRepository.enterChatRoom(
                    onComplete = {
                        setEnterRoomLoadingState(false)
                    },
                    onError = {
                        toggleEnterChatRoomStateToggle("네트워크 연결을 다시 확인해주세요")
                    },
                    postId = partyItem.value?.postId.toString(),
                    postUserId = partyItem.value?.userId.toString(),
                    myUserId = myUserInfo?.userId.toString(),
                    restaurantName = partyItem.value?.restaurantName!!,
                    createdChatRoom = getCurrentTime()
                ).collectLatest { response ->
                    if (response is ApiResultSuccess) {
                        NavigationUtils.navigate(
                            navController, DetailScreen.ChatDetail.navigateWithArg(
                                partyItem.value?.postId.toString()
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

    private fun observeChatRoomChangesListener() {
        viewModelScope.launch {
            partyItem.collectLatest { partyItem ->
                if (partyItem !=  null) {
                    setChatRoomListLoadingState(true)
                    observeChatRoomChangesListener =
                        chatDBRepository.addChatRoomsEventListener(
                            onComplete = {
                                setChatRoomListLoadingState(false)
                            },
                            onError = {
                                toggleChatRoomListNetworkErrorToggle()
                            },
                            partyItem.postId.toString()
                        ) { chatRoom ->
                            _chatRoomItem.value = chatRoom
                        }
                }
            }
        }
    }

    private fun removeObserveChatRoomChangesListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(observeChatRoomChangesListener)
        }
    }

    private fun toggleEnterChatRoomStateToggle(errorMessage: String) {
        viewModelScope.launch {
            _isEnterChatRoom.emit(true)
            _showEnterChatRoom.value = !showEnterChatRoom.value
            _enterRoomErrorMessage.value = errorMessage
        }
    }

    private fun setEnterRoomLoadingState(isLoading: Boolean) {
        _isEnterRoomLoadingView.value = isLoading
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

    override fun onCleared() {
        removeObserveChatRoomChangesListener()
        super.onCleared()
    }
}
