package com.mbj.doeat.ui.screen.home.chat_detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.BottomBarScreen
import com.mbj.doeat.util.DateUtils
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatDBRepository: ChatDBRepository,
    private val defaultDBRepository: DefaultDBRepository
) :
    ViewModel() {

    private val _postId = MutableStateFlow("")
    val postId: StateFlow<String> = _postId

    private val _sendMessage = MutableStateFlow("")
    val sendMessage: StateFlow<String> = _sendMessage

    private val _chatItemList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatItemList: StateFlow<List<ChatItem>> = _chatItemList

    private val _chatRoomMembers = MutableStateFlow<List<LoginResponse>>(emptyList())
    val chatRoomMembers: StateFlow<List<LoginResponse>> = _chatRoomMembers

    private val _chatRoomItem = MutableStateFlow<ChatRoom?>(null)
    val chatRoomItem: StateFlow<ChatRoom?> = _chatRoomItem

    private val myUserInfo = UserDataStore.getLoginResponse()
    private var inMemberKey = ""

    private var observeChatChangesListener: ChildEventListener? = null
    private var observeParticipantsChangesListener: ValueEventListener? = null

    init {
        observeChatChangesListener()
        getChatRoomItem()
        observeParticipantsChangesListener()
    }

    fun updatePostId(postId: String) {
        _postId.value = postId
    }

    fun changeSendMessage(newMessage: String) {
        _sendMessage.value = newMessage
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatDBRepository.sendMessage(
                onComplete = { },
                onError = { },
                postId = postId.value.substring(1, postId.value.length - 1),
                message = message,
                sendMessageTime = DateUtils.getCurrentTime(),
            ).collectLatest { }
        }
    }

    private fun observeChatChangesListener() {
        viewModelScope.launch {
            postId.collectLatest { postId ->
                if (postId != "") {
                    observeChatChangesListener = chatDBRepository.addChatDetailEventListener(
                        postId.substring(1, postId.length - 1)
                    ) { chatItem ->
                        val currentList = _chatItemList.value
                        val newList = currentList.toMutableList().apply { add(chatItem) }
                        _chatItemList.value = newList
                    }
                }
            }
        }
    }

    private fun getChatRoomItem() {
        viewModelScope.launch {
            postId.collectLatest { postId ->
                if (postId != "") {
                    chatDBRepository.getChatRoomItem(
                        onComplete = { },
                        onError = { },
                        postId = postId.substring(1, postId.length - 1)
                    ) { chatRoomItem ->
                        _chatRoomItem.value = chatRoomItem
                    }
                }
            }
        }
    }

    fun leaveChatRoom(navController: NavHostController) {
        viewModelScope.launch {
            chatDBRepository.leaveChatRoom(
                onComplete = { },
                onError = { },
                postId = postId.value.substring(1, postId.value.length - 1),
                inMemberKey = inMemberKey,
                chatItemList = chatItemList.value
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    navController.popBackStack()
                    navController.navigate(BottomBarScreen.Community.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    private fun removeObserveChatChangesListener() {
        chatDBRepository.removeChatDetailEventListener(
            postId.value.substring(1, postId.value.length - 1),
            observeChatChangesListener
        )
    }

    private fun observeParticipantsChangesListener() {
        viewModelScope.launch {
            postId.collectLatest { postId ->
                if (postId != "") {
                    observeParticipantsChangesListener =
                        chatDBRepository.addChatRoomsEventListener(
                            onComplete = { },
                            onError = { },
                            postId.substring(1, postId.length - 1)
                        ) { chatRoom ->
                            getInMemberKey(chatRoom)
                            getChatRoomMembers(chatRoom)
                        }
                }
            }
        }
    }

    private fun removeObserveParticipantsChangesListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(observeParticipantsChangesListener)
        }
    }

    private fun getChatRoomMembers(chatRoom: ChatRoom?) {
        viewModelScope.launch {
            defaultDBRepository.getAllUserList(
                onComplete = { },
                onError = { }
            ).collectLatest { loginList ->
                if (loginList is ApiResultSuccess) {
                    val membersList = chatRoom?.members?.values?.map { inMember ->
                        val userId = inMember.userId
                        loginList.data.find { it.userId.toString() == userId }
                    }
                    if (!membersList.isNullOrEmpty()) {
                        _chatRoomMembers.value = membersList as List<LoginResponse>
                    }
                }
            }
        }
    }

    private fun getInMemberKey(chatRoom: ChatRoom?) {
        val inMemberKeyFind = chatRoom?.members?.entries
            ?.firstOrNull { it.value.userId == myUserInfo?.userId.toString() }
            ?.key

        if (inMemberKeyFind != null) {
            inMemberKey = inMemberKeyFind
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            removeObserveChatChangesListener()
            removeObserveParticipantsChangesListener()
        }
    }
}
