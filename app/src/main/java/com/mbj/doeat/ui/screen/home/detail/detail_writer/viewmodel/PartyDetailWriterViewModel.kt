package com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _isDeletePartyLoadingView = MutableStateFlow<Boolean>(false)
    val isDeletePartyLoadingView: StateFlow<Boolean> = _isDeletePartyLoadingView

    private val _isDeletePartyNetworkError = MutableSharedFlow<Boolean>()
    val isDeletePartyNetworkError: SharedFlow<Boolean> = _isDeletePartyNetworkError.asSharedFlow()

    private val _showDeletePartyListNetworkError = MutableStateFlow<Boolean>(false)
    val showDeletePartyListNetworkError: StateFlow<Boolean> = _showDeletePartyListNetworkError

    private val _isEnterChatRoom = MutableSharedFlow<Boolean>()
    val isEnterChatRoom: SharedFlow<Boolean> = _isEnterChatRoom.asSharedFlow()

    private val _showEnterChatRoom = MutableStateFlow<Boolean>(false)
    val showEnterChatRoom: StateFlow<Boolean> = _showEnterChatRoom

    private val _isEnterRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isEnterRoomLoadingView: StateFlow<Boolean> = _isEnterRoomLoadingView

    private val _isDeleteChatRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isDeleteChatRoomLoadingView: StateFlow<Boolean> = _isDeleteChatRoomLoadingView


    private var observeChatRoomChangesListener: ValueEventListener? = null

    init {
        observeChatRoomChangesListener()
    }

    fun updateSearchItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
    }

    fun changeShowDeletePartyDialog(showDialog: Boolean) {
        _showDeletePartyDialog.value = showDialog
    }

    fun enterChatRoom(navController: NavHostController) {
        viewModelScope.launch {
            setEnterRoomLoadingState(true)
            val myUserInfo = UserDataStore.getLoginResponse()

            val isUserInChatRoom =
                chatRoomItem.value?.members?.any { it.value.userId == myUserInfo?.userId.toString() }

            if (isUserInChatRoom == true) {
                setEnterRoomLoadingState(false)
                NavigationUtils.navigate(
                    navController, DetailScreen.ChatDetail.navigateWithArg(
                        partyItem.value?.postId.toString()
                    )
                )
            } else {
                chatDBRepository.enterChatRoom(
                    onComplete = {
                        setEnterRoomLoadingState(false)
                    },
                    onError = {
                        toggleEnterChatRoomStateToggle()
                    },
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
    }

    fun deleteParty(navHostController: NavHostController) {
        setDeletePartyLoadingState(true)
        viewModelScope.launch {
            defaultDBRepository.deleteParty(
                PartyPostIdRequestDto(partyItem.value?.postId!!),
                onComplete = {
                    setDeletePartyLoadingState(false)
                },
                onError = {
                    toggleDeletePartyNetworkErrorToggle()
                }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    deleteChatRoom(
                        postId = partyItem.value!!.postId.toString(),
                        navHostController = navHostController
                    )
                }
            }
        }
    }

    private fun deleteChatRoom(postId: String, navHostController: NavHostController) {
        viewModelScope.launch {
            setDeleteChatRoomLoadingState(true)
            chatDBRepository.deleteChatRoom(
                onComplete = {
                    setDeleteChatRoomLoadingState(false)
                },
                onError = { },
                postId = postId
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

    private fun observeChatRoomChangesListener() {
        viewModelScope.launch {
            partyItem.collectLatest { partyItem ->
                if (partyItem != null) {
                    observeChatRoomChangesListener =
                        chatDBRepository.addChatRoomsEventListener(
                            onComplete = { },
                            onError = { },
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

    private fun setDeletePartyLoadingState(isLoading: Boolean) {
        _isDeletePartyLoadingView.value = isLoading
    }

    private fun toggleEnterChatRoomStateToggle() {
        viewModelScope.launch {
            _isEnterChatRoom.emit(true)
            _showEnterChatRoom.value = !showEnterChatRoom.value
        }
    }

    private fun setEnterRoomLoadingState(isLoading: Boolean) {
        _isEnterRoomLoadingView.value = isLoading
    }

    private fun toggleDeletePartyNetworkErrorToggle() {
        viewModelScope.launch {
            _isDeletePartyNetworkError.emit(true)
            _showDeletePartyListNetworkError.value = !showDeletePartyListNetworkError.value
        }
    }

    private fun setDeleteChatRoomLoadingState(isLoading: Boolean) {
        _isDeleteChatRoomLoadingView.value = isLoading
    }

    override fun onCleared() {
        removeObserveChatRoomChangesListener()
        super.onCleared()
    }
}
