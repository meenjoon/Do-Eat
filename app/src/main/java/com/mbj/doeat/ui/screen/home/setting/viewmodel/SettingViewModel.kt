package com.mbj.doeat.ui.screen.home.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.UserIdRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val chatDBRepository: ChatDBRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow<LoginResponse?>(null)
    val userInfo: StateFlow<LoginResponse?> = _userInfo

    private val _myPartyList = MutableStateFlow<List<Party>>(emptyList())
    val myPartyList: StateFlow<List<Party>> = _myPartyList

    private val _chatRoomItemList = MutableStateFlow<List<ChatRoom>?>(emptyList())
    val chatRoomItemList: StateFlow<List<ChatRoom>?> = _chatRoomItemList

    private var chatRoomsAllEventListener: ValueEventListener? = null

    init {
        getUserInfo()
        getMyPartyList()
        addChatRoomsAllEventListener()
    }

    private fun getUserInfo() {
        _userInfo.value = UserDataStore.getLoginResponse()
    }

    private fun getMyPartyList() {
        viewModelScope.launch {
            userInfo.collectLatest { userInfo ->
                if (userInfo != null) {
                    defaultDBRepository.getMyPartyList(
                        onComplete = { },
                        onError = { },
                        userIdRequest = UserIdRequest(userInfo.userId!!)
                    ).collectLatest { partyList ->
                        if (partyList is ApiResultSuccess) {
                            _myPartyList.value = partyList.data
                        }
                    }
                }
            }
        }
    }

    private fun addChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatRoomsAllEventListener =
                chatDBRepository.addChatRoomsAllEventListener(
                    onComplete = { },
                    onError = { }
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

    override fun onCleared() {
        super.onCleared()
        removeChatRoomsAllEventListener()
    }
}
