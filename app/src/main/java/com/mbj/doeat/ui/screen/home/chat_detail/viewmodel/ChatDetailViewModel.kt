package com.mbj.doeat.ui.screen.home.chat_detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.util.DateUtils
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(private val chatDBRepository: ChatDBRepository) :
    ViewModel() {

    private val myUserInfo = UserDataStore.getLoginResponse()

    private val _postId = MutableStateFlow("")
    val postId: StateFlow<String> = _postId

    private val _sendMessage = MutableStateFlow("")
    val sendMessage: StateFlow<String> = _sendMessage

    private val _chatItemList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatItemList: StateFlow<List<ChatItem>> = _chatItemList

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
                myUserId = myUserInfo?.userId.toString(),
                sendMessageTime = DateUtils.getCurrentTime()
            ).collectLatest { }
        }
    }
}
