package com.mbj.doeat.ui.screen.home.detail.detail_participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.ui.component.PartyDetailContent
import com.mbj.doeat.ui.component.ReusableWebView
import com.mbj.doeat.ui.component.button.BackButton
import com.mbj.doeat.ui.component.button.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.toast.ToastMessage
import com.mbj.doeat.ui.screen.home.detail.detail_participant.viewmodel.PartyDetailParticipantViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700
import com.mbj.doeat.ui.theme.button1

@Composable
fun PartyDetailParticipantScreen(
    party: Party,
    navController: NavHostController,
    onClick: () -> Unit
) {

    val viewModel: PartyDetailParticipantViewModel = hiltViewModel()
    viewModel.updatePartyItem(party)

    val partyItemState by viewModel.partyItem.collectAsStateWithLifecycle()
    val chatRoomItem by viewModel.chatRoomItem.collectAsStateWithLifecycle()
    val showEnterChatRoomState by viewModel.showEnterChatRoom.collectAsStateWithLifecycle()
    val isEnterChatRoomState by viewModel.isEnterChatRoom.collectAsStateWithLifecycle(initialValue = false)

    Scaffold(
        bottomBar = {
            LongRectangleButtonWithParams(
                text = "참가하기",
                height = 60.dp,
                useFillMaxWidth = true,
                padding = PaddingValues(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
                backgroundColor = if (chatRoomItem?.members?.count() == party.recruitmentLimit) {
                    Color.Red
                } else {
                    Yellow700
                },
                contentColor = if (chatRoomItem?.members?.count() == party.recruitmentLimit) {
                    Color.White
                } else {
                    Color.Black
                },
                textStyle = MaterialTheme.typography.button1
            ) {
                viewModel.enterChatRoom(navController = navController)
            }
        }
    ) { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(navController = navController)
                }

                ReusableWebView(
                    url = partyItemState?.link,
                    restaurantName = partyItemState?.restaurantName!!,
                    webViewModifier = Modifier.fillMaxHeight(0.5f),
                ) {}

                PartyDetailContent(party = partyItemState, chatRoom = chatRoomItem)
            }

            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                showToast = showEnterChatRoomState,
                showMessage = isEnterChatRoomState,
                message = "현재 인원이 꽉 찼습니다."
            )
        }
    }
}
