package com.mbj.doeat.ui.screen.home.detail.detail_participant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.network.adapter.ApiResultSuccess
import com.mbj.doeat.data.remote.network.api.chat_db.repository.ChatDBRepository
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.util.DateUtils.getCurrentTime
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyDetailParticipantViewModel @Inject constructor(
    private val chatDBRepository: ChatDBRepository
) : ViewModel() {

    private val _partyItem = MutableStateFlow<Party?>(null)
    val partyItem: StateFlow<Party?> = _partyItem

    fun updatePartyItem(inputPartyItem: Party) {
        _partyItem.value = inputPartyItem
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
        }
    }
}
