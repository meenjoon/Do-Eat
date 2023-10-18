package com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.PartyPostIdRequestDto
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.BottomBarScreen
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.util.DateUtils
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyDetailWriterViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val chatDBRepository: ChatDBRepository
) : ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    private val _chatRoomItem = MutableStateFlow<ChatRoom?>(null)
    val chatRoomItem: StateFlow<ChatRoom?> = _chatRoomItem

    private val _showDeletePartyDialog = MutableStateFlow<Boolean>(false)
    val showDeletePartyDialog: StateFlow<Boolean> = _showDeletePartyDialog

    private val _isLoadingView = MutableStateFlow<Boolean>(false)
    val isLoadingView: StateFlow<Boolean> = _isLoadingView

    init {
        getChatRoomItem()
    }

    fun updateSearchItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }

    fun changeShowDeletePartyDialog(showDialog: Boolean) {
        _showDeletePartyDialog.value = showDialog
    }

    fun enterChatRoom(navController: NavHostController) {
        viewModelScope.launch {
            val myUserInfo = UserDataStore.getLoginResponse()

            chatDBRepository.enterChatRoom(
                onComplete = { },
                onError = { },
                postId = partyItem.value?.postId.toString(),
                postUserId = partyItem.value?.userId.toString(),
                myUserId = myUserInfo?.userId.toString(),
                restaurantName = partyItem.value?.restaurantName!!,
                createdChatRoom = DateUtils.getCurrentTime()
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    NavigationUtils.navigate(
                        navController, DetailScreen.ChatDetail.navigateWithArg(
                            partyItem.value?.postId.toString()
                        )
                    )
                }
            }
        }
    }

    fun deleteParty(navHostController: NavHostController) {
        setLoadingState(true)
        viewModelScope.launch {
            defaultDBRepository.partyDelete(
                PartyPostIdRequestDto(partyItem.value?.postId!!),
                onComplete = { },
                onError = { }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    navHostController.navigate(BottomBarScreen.Community.route) {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                    }
                }
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

    private fun setLoadingState(isLoading: Boolean) {
        _isLoadingView.value = isLoading
    }
}
