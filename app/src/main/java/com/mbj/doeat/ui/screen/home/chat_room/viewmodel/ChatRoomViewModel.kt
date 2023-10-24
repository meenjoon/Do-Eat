package com.mbj.doeat.ui.screen.home.chat_room.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.InMember
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.DetailScreen
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
class ChatRoomViewModel @Inject constructor(
    private val chatDBRepository: ChatDBRepository,
    private val defaultDBRepository: DefaultDBRepository
) : ViewModel() {

    private val _myChatRoomList = MutableStateFlow<List<ChatRoom>?>(emptyList())
    val myChatRoomList: StateFlow<List<ChatRoom>?> = _myChatRoomList

    private val _userList = MutableStateFlow<List<LoginResponse>?>(emptyList())
    val userList: StateFlow<List<LoginResponse>?> = _userList

    private val _isChatRoomListNetworkError = MutableSharedFlow<Boolean>()
    val isChatRoomListNetworkError: SharedFlow<Boolean> = _isChatRoomListNetworkError.asSharedFlow()

    private val _showChatRoomListNetworkError = MutableStateFlow<Boolean>(false)
    val showChatRoomListNetworkError: StateFlow<Boolean> = _showChatRoomListNetworkError

    private val myUserId = UserDataStore.getLoginResponse()?.userId

    private var chatRoomsAllEventListener: ValueEventListener? = null

    init {
        addChatRoomsAllEventListener()
        getUserList()
    }

    private fun addChatRoomsAllEventListener() {
        chatRoomsAllEventListener =
            chatDBRepository.addChatRoomsAllEventListener(
                onComplete = { },
                onError = {
                    toggleChatRoomListNetworkErrorToggle()
                }
            ) { chatRoomList ->
                myUserId?.let { userId ->
                    chatRoomList?.let { chatRooms ->
                        val myChatRoom = chatRoomList.filter { chatRoom ->
                            chatRoom.members?.values?.any { inMember -> inMember.userId == userId.toString() } == true
                        }
                        val sortedChatRooms = myChatRoom.sortedByDescending { chatRoom ->
                            chatRoom.lastMessageDate ?: chatRoom.createdChatRoomDate
                        }
                        _myChatRoomList.value = sortedChatRooms
                    }
                }
            }
    }

    private fun removeChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(chatRoomsAllEventListener)
        }
    }

    private fun getUserList() {
        viewModelScope.launch {
            defaultDBRepository.getAllUserList(
                onComplete = { },
                onError = { }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    _userList.value = response.data
                }
            }
        }
    }

    fun chatRoomImages(userList: List<LoginResponse>?, members: Map<String, InMember>?): List<String> {
        return userList
            ?.mapNotNull { user ->
                val matchingInMember = members?.values?.find { it.userId == user.userId?.toString() }
                matchingInMember?.let { user.userImageUrl }
            }
            ?: emptyList()
    }

    fun enterChatRoom(
        chatRoom: ChatRoom,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            NavigationUtils.navigate(
                navController, DetailScreen.ChatDetail.navigateWithArg(
                    chatRoom.postId.toString()
                )
            )
        }
    }

    private fun toggleChatRoomListNetworkErrorToggle() {
        viewModelScope.launch {
            _isChatRoomListNetworkError.emit(true)
            _showChatRoomListNetworkError.value = !showChatRoomListNetworkError.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeChatRoomsAllEventListener()
    }
}
