package com.mbj.doeat.ui.screen.home.detail.detail_participant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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
class PartyDetailParticipantViewModel @Inject constructor(private val chatDBRepository: ChatDBRepository) :
    ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    private val _chatRoomItem = MutableStateFlow<ChatRoom?>(null)
    val chatRoomItem: StateFlow<ChatRoom?> = _chatRoomItem

    private val _isEnterChatRoom = MutableSharedFlow<Boolean>()
    val isEnterChatRoom: SharedFlow<Boolean> = _isEnterChatRoom.asSharedFlow()

    private val _showEnterChatRoom = MutableStateFlow<Boolean>(false)
    val showEnterChatRoom: StateFlow<Boolean> = _showEnterChatRoom

    init {
        getChatRoomItem()
    }

    fun updatePartyItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }

    fun enterChatRoom(navController: NavHostController) {
        viewModelScope.launch {
            val myUserInfo = UserDataStore.getLoginResponse()

            val isChatRoomFull = chatRoomItem.value?.members?.count() == partyItem.value?.recruitmentLimit
            val isUserInChatRoom = chatRoomItem.value?.members?.contains(myUserInfo?.userId.toString()) == true

            if (isUserInChatRoom || !isChatRoomFull) {
                chatDBRepository.enterChatRoom(
                    onComplete = { },
                    onError = { },
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
                toggleEnterChatRoomToggle()
            }
        }
    }

    private fun getChatRoomItem() {
        viewModelScope.launch {
            partyItem.collectLatest { partyItem ->
                if (partyItem != null) {
                    chatDBRepository.getChatRoomItem(
                        onComplete = { },
                        onError = { },
                        postId = partyItem.postId.toString()
                    ) { chatRoomItem ->
                        _chatRoomItem.value = chatRoomItem
                    }
                }
            }
        }
    }

    private fun toggleEnterChatRoomToggle() {
        viewModelScope.launch {
            _isEnterChatRoom.emit(true)
            _showEnterChatRoom.value = !showEnterChatRoom.value
        }
    }
}
