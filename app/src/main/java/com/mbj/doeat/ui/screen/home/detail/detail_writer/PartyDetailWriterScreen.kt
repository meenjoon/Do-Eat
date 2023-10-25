package com.mbj.doeat.ui.screen.home.detail.detail_writer

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
import com.mbj.doeat.ui.component.button.BackButton
import com.mbj.doeat.ui.component.loading.LoadingView
import com.mbj.doeat.ui.component.button.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.party.PartyDetailContent
import com.mbj.doeat.ui.component.webview.ReusableWebView
import com.mbj.doeat.ui.component.dialog.YesNoDialog
import com.mbj.doeat.ui.component.toast.ToastMessage
import com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel.PartyDetailWriterViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Red500
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700
import com.mbj.doeat.ui.theme.button1

@Composable
fun PartyDetailWriterScreen(party: Party, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: PartyDetailWriterViewModel = hiltViewModel()
    viewModel.updateSearchItem(party)

    val partyItemState by viewModel.partyItem.collectAsStateWithLifecycle()
    val chatRoomItem by viewModel.chatRoomItem.collectAsStateWithLifecycle()
    val showCreatePartyDialogState by viewModel.showDeletePartyDialog.collectAsStateWithLifecycle()
    val isDeletePartyLoadingViewState by viewModel.isDeletePartyLoadingView.collectAsStateWithLifecycle()
    val isDeletePartyNetworkErrorState by viewModel.isDeletePartyNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showDeletePartyListNetworkErrorState by viewModel.showDeletePartyListNetworkError.collectAsStateWithLifecycle()
    val showEnterChatRoomState by viewModel.showEnterChatRoom.collectAsStateWithLifecycle()
    val isEnterChatRoomState by viewModel.isEnterChatRoom.collectAsStateWithLifecycle(initialValue = false)
    val isEnterRoomLoadingViewState by viewModel.isEnterRoomLoadingView.collectAsStateWithLifecycle()
    val isDeleteChatRoomLoadingViewState by viewModel.isDeleteChatRoomLoadingView.collectAsStateWithLifecycle()
    val isDeleteChatRoomNetworkErrorState by viewModel.isDeleteChatRoomNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showDeleteChatRoomNetworkErrorState by viewModel.showDeleteChatRoomNetworkError.collectAsStateWithLifecycle()
    val isChatRoomListLoadingViewState by viewModel.isChatRoomListLoadingView.collectAsStateWithLifecycle()
    val isChatRoomListNetworkErrorState by viewModel.isChatRoomListNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showChatRoomListNetworkErrorState by viewModel.showChatRoomListNetworkError.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LongRectangleButtonWithParams(
                    text = "삭제하기",
                    width = 120.dp,
                    height = 60.dp,
                    useFillMaxWidth = false,
                    padding = PaddingValues(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
                    backgroundColor = Red500,
                    contentColor = Color.White,
                    textStyle = MaterialTheme.typography.button1
                ) {
                    viewModel.changeShowDeletePartyDialog(showDialog = true)
                }

                LongRectangleButtonWithParams(
                    text = "채팅하기",
                    width = 120.dp,
                    height = 60.dp,
                    useFillMaxWidth = false,
                    padding = PaddingValues(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
                    backgroundColor = Yellow700,
                    contentColor = Color.Black,
                    textStyle = MaterialTheme.typography.button1
                ) {
                    viewModel.enterChatRoom(navController)
                }
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

            YesNoDialog(
                showDialog = showCreatePartyDialogState,
                onYesClick = { viewModel.deleteParty(navController) },
                onNoClick = { viewModel.changeShowDeletePartyDialog(showDialog = false) },
                title = "파티를 삭제 하시겠습니까?",
                message = "파티가 삭제됩니다.",
                confirmButtonMessage = "삭제",
                dismissButtonMessage = "취소"
            )

            LoadingView(
                isLoading = isDeletePartyLoadingViewState,
            )

            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                showToast = showEnterChatRoomState,
                showMessage = isEnterChatRoomState,
                message = "네트워크 연결을 다시 확인해주세요"
            )

            LoadingView(
                isLoading = isEnterRoomLoadingViewState
            )

            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                showToast = showDeletePartyListNetworkErrorState,
                showMessage = isDeletePartyNetworkErrorState,
                message = "네트워크 연결을 다시 확인해주세요"
            )

            LoadingView(
                isLoading = isDeleteChatRoomLoadingViewState
            )

            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                showToast = showDeleteChatRoomNetworkErrorState,
                showMessage = isDeleteChatRoomNetworkErrorState,
                message = "네트워크 연결을 다시 확인해주세요"
            )

            LoadingView(
                isLoading = isChatRoomListLoadingViewState
            )

            ToastMessage(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                showToast = showChatRoomListNetworkErrorState,
                showMessage = isChatRoomListNetworkErrorState,
                message = "네트워크 연결을 다시 확인해주세요"
            )
        }
    }
}
