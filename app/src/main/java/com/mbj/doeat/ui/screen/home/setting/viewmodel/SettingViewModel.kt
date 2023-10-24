package com.mbj.doeat.ui.screen.home.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.ValueEventListener
import com.mbj.doeat.data.local.user_pref.repository.UserPreferenceRepository
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.UserIdRequest
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.data.remote.network.api.default_db.repository.DefaultDBRepository
import com.mbj.doeat.ui.graph.AuthScreen
import com.mbj.doeat.ui.graph.DetailScreen
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
class SettingViewModel @Inject constructor(
    private val defaultDBRepository: DefaultDBRepository,
    private val chatDBRepository: ChatDBRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _myCreatedParties = MutableStateFlow<List<Party>>(emptyList())
    val myCreatedParties: StateFlow<List<Party>> = _myCreatedParties

    private val _joinedParties = MutableStateFlow<List<Party>>(emptyList())
    val joinedParties: StateFlow<List<Party>> = _joinedParties

    private val _chatRoomItemList = MutableStateFlow<List<ChatRoom>?>(emptyList())
    val chatRoomItemList: StateFlow<List<ChatRoom>?> = _chatRoomItemList

    private val _showLogoutDialog = MutableStateFlow<Boolean>(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    private val _showWithdrawMembershipDialog = MutableStateFlow<Boolean>(false)
    val showWithdrawMembershipDialog: StateFlow<Boolean> = _showWithdrawMembershipDialog

    private val _myJoinedChatRoomPostIds = MutableStateFlow<Set<String>?>(null)
    private val myJoinedChatRoomPostIds: StateFlow<Set<String>?> = _myJoinedChatRoomPostIds

    private val _myPartyPostIds = MutableStateFlow<Set<String>?>(null)
    private val myPartyPostIds: StateFlow<Set<String>?> = _myPartyPostIds

    private val _isMyCreatedPartiesNetworkError = MutableSharedFlow<Boolean>()
    val isMyCreatedPartiesNetworkError: SharedFlow<Boolean> = _isMyCreatedPartiesNetworkError.asSharedFlow()

    private val _showMyCreatedPartiesNetworkError = MutableStateFlow<Boolean>(false)
    val showMyCreatedPartiesNetworkError: StateFlow<Boolean> = _showMyCreatedPartiesNetworkError

    private val _isMyCreatedPartiesLoadingView = MutableStateFlow<Boolean>(false)
    val isMyCreatedPartiesLoadingView: StateFlow<Boolean> = _isMyCreatedPartiesLoadingView

    private val _isAllPartyListNetworkError = MutableSharedFlow<Boolean>()
    val isAllPartyListNetworkError: SharedFlow<Boolean> = _isAllPartyListNetworkError.asSharedFlow()

    private val _showAllPartyListNetworkError = MutableStateFlow<Boolean>(false)
    val showAllPartyListNetworkError: StateFlow<Boolean> = _showAllPartyListNetworkError

    private val _isAllPartyListLoadingView = MutableStateFlow<Boolean>(false)
    val isAllPartyListLoadingView: StateFlow<Boolean> = _isAllPartyListLoadingView

    private val _isEnterChatRoom = MutableSharedFlow<Boolean>()
    val isEnterChatRoom: SharedFlow<Boolean> = _isEnterChatRoom.asSharedFlow()

    private val _showEnterChatRoom = MutableStateFlow<Boolean>(false)
    val showEnterChatRoom: StateFlow<Boolean> = _showEnterChatRoom

    private val _isEnterRoomLoadingView = MutableStateFlow<Boolean>(false)
    val isEnterRoomLoadingView: StateFlow<Boolean> = _isEnterRoomLoadingView

    private val _isWithdrawMembershipNetworkError = MutableSharedFlow<Boolean>()
    val isWithdrawMembershipNetworkError: SharedFlow<Boolean> = _isWithdrawMembershipNetworkError.asSharedFlow()

    private val _showWithdrawMembershipNetworkError = MutableStateFlow<Boolean>(false)
    val showWithdrawMembershipNetworkError: StateFlow<Boolean> = _showWithdrawMembershipNetworkError

    val userInfo = UserDataStore.getLoginResponse()
    private var chatRoomsAllEventListener: ValueEventListener? = null

    init {
        getMyPartyList()
        observeMyJoinedChatRoomPostIds()
        addChatRoomsAllEventListener()
    }

    private fun getMyPartyList() {
        viewModelScope.launch {
            setMyCreatedPartiesLoadingState(true)
            if (userInfo != null) {
                defaultDBRepository.getMyPartyList(
                    onComplete = {
                        setMyCreatedPartiesLoadingState(false)
                    },
                    onError = {
                        toggleMyCreatedPartiesNetworkErrorToggle()
                    },
                    userIdRequest = UserIdRequest(userInfo.userId!!)
                ).collectLatest { partyList ->
                    if (partyList is ApiResultSuccess) {
                        _myCreatedParties.value = partyList.data
                    }
                }
            }
        }
    }

    private fun getAllPartyList(postIdSet: Set<String>?) {
        viewModelScope.launch {
            setAllPartyListLoadingState(true)
            if (userInfo != null) {
                defaultDBRepository.getAllPartyList(
                    onComplete = {
                        setAllPartyListLoadingState(false)
                    },
                    onError = {
                        toggleAllPartyListNetworkErrorToggle()
                    },
                ).collectLatest { partyList ->
                    if (partyList is ApiResultSuccess) {
                        _joinedParties.value = filterPartiesByPostIds(partyList.data, postIdSet ?: emptySet())
                        _myPartyPostIds.value = getPostIdSetFromPartyList(partyList.data)
                    }
                }
            }
        }
    }

    private fun observeMyJoinedChatRoomPostIds() {
        viewModelScope.launch {
            myJoinedChatRoomPostIds.collectLatest { postIdSet ->
                getAllPartyList(postIdSet = postIdSet)
            }
        }
    }

    fun onDetailInfoClick(party: Party, navController: NavHostController) {
        val encodedLink = UrlUtils.encodeUrl(party.link)
        val titleWithoutHtmlTags = MapConverter.removeHtmlTags(party.restaurantName)
        if (userInfo?.userId == party.userId) {
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
            if (userInfo != null) {
                setEnterRoomLoadingState(true)
                val chatRoom = chatRoomItemList?.find { it.postId == party.postId.toString() }

                val isUserInChatRoom =
                    chatRoom?.members?.any { it.value.userId == userInfo.userId.toString() }

                if (isUserInChatRoom == true) {
                    setEnterRoomLoadingState(false)
                    NavigationUtils.navigate(
                        navController, DetailScreen.ChatDetail.navigateWithArg(
                            party.postId.toString()
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
                        postId = party.postId.toString(),
                        postUserId = party.userId.toString(),
                        myUserId = userInfo.userId.toString(),
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
                }
            }
        }
    }

    fun logout(navController: NavHostController) {
        UserDataStore.removeLoginResponse()
        userPreferenceRepository.saveAutoLoginState(false)
        navController.navigate(AuthScreen.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun withdrawMembership(navController: NavHostController) {
        viewModelScope.launch {
            if (userInfo != null) {
                defaultDBRepository.deleteUser(
                    onComplete = { },
                    onError = {
                        toggleWithdrawMembershipNetworkErrorToggle()
                    },
                    userIdRequest = UserIdRequest(userInfo.userId!!)
                ).collectLatest { response ->
                    if (response is ApiResultSuccess) {
                        deleteChatRoom(
                            myUserId = userInfo.userId.toString(),
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun deleteChatRoom(myUserId: String, navController: NavHostController) {
        viewModelScope.launch {
            myPartyPostIds.value?.let {
                chatDBRepository.deleteAllChatRoomsForUserID(
                    userIdToDelete = myUserId,
                    postIdsToDelete = it
                ) { response ->
                    if (response != null) {
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getMyJoinedChatRoomPostIds(
        chatRoomList: List<ChatRoom>,
        myUserId: String
    ): Set<String> {
        val myJoinedChatRooms = chatRoomList.filter { chatRoom ->
            chatRoom.members?.values?.any { inMember -> inMember.userId == myUserId } == true
        }
        return myJoinedChatRooms.mapNotNull { it.postId }.toSet()
    }

    private fun filterPartiesByPostIds(parties: List<Party>, postIds: Set<String>): List<Party> {
        return parties.filter { party ->
            party.postId.toString() in postIds
        }
    }

    private fun getPostIdSetFromPartyList(partyList: List<Party>): Set<String> {
        val postIdSet = mutableSetOf<String>()

        for (party in partyList) {
            val postId = party.postId.toString()
            postIdSet.add(postId)
        }

        return postIdSet
    }

    fun changeShowLogoutDialog(showDialog: Boolean) {
        _showLogoutDialog.value = showDialog
    }

    fun changeShowWithdrawMembershipDialog(showDialog: Boolean) {
        _showWithdrawMembershipDialog.value = showDialog
    }

    private fun addChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatRoomsAllEventListener =
                chatDBRepository.addChatRoomsAllEventListener(
                    onComplete = { },
                    onError = { }
                ) { chatRoomList ->
                    _chatRoomItemList.value = chatRoomList
                    if (chatRoomList != null) {
                        _myJoinedChatRoomPostIds.value = getMyJoinedChatRoomPostIds(
                            chatRoomList,
                            userInfo?.userId.toString()
                        )
                    }
                }
        }
    }

    private fun removeChatRoomsAllEventListener() {
        viewModelScope.launch {
            chatDBRepository.removeChatRoomsAllEventListener(chatRoomsAllEventListener)
        }
    }

    private fun toggleMyCreatedPartiesNetworkErrorToggle() {
        viewModelScope.launch {
            _isMyCreatedPartiesNetworkError.emit(true)
            _showMyCreatedPartiesNetworkError.value = !showMyCreatedPartiesNetworkError.value
        }
    }

    private fun setMyCreatedPartiesLoadingState(isLoading: Boolean) {
        _isMyCreatedPartiesLoadingView.value = isLoading
    }

    private fun toggleAllPartyListNetworkErrorToggle() {
        viewModelScope.launch {
            _isAllPartyListNetworkError.emit(true)
            _showAllPartyListNetworkError.value = !showAllPartyListNetworkError.value
        }
    }

    private fun setAllPartyListLoadingState(isLoading: Boolean) {
        _isAllPartyListLoadingView.value = isLoading
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

    private fun toggleWithdrawMembershipNetworkErrorToggle() {
        viewModelScope.launch {
            _isWithdrawMembershipNetworkError.emit(true)
            _showWithdrawMembershipNetworkError.value = !showWithdrawMembershipNetworkError.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeChatRoomsAllEventListener()
    }
}
