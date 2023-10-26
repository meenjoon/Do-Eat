package com.mbj.doeat.ui.screen.home.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.ui.component.party.HomeDetailPartyContent
import com.mbj.doeat.ui.component.dialog.YesNoDialog
import com.mbj.doeat.ui.component.divider.HorizontalDivider
import com.mbj.doeat.ui.component.loading.LoadingView
import com.mbj.doeat.ui.component.party.NoPartiesAvailable
import com.mbj.doeat.ui.component.toast.ToastMessage
import com.mbj.doeat.ui.screen.home.setting.viewmodel.SettingViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Brown900
import com.mbj.doeat.ui.theme.Color.Companion.SettingDividerColor
import com.mbj.doeat.ui.theme.Color.Companion.SettingScreenColor

@Composable
fun SettingScreen(name: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: SettingViewModel = hiltViewModel()

    val myCreatedPartiesState by viewModel.myCreatedParties.collectAsStateWithLifecycle()
    val joinedParties by viewModel.joinedParties.collectAsStateWithLifecycle()
    val chatRoomItemListState by viewModel.chatRoomItemList.collectAsStateWithLifecycle()
    val showLogoutDialogState by viewModel.showLogoutDialog.collectAsStateWithLifecycle()
    val showWithdrawMembershipDialogState by viewModel.showWithdrawMembershipDialog.collectAsStateWithLifecycle()
    val isMyCreatedPartiesNetworkErrorState by viewModel.isMyCreatedPartiesNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showMyCreatedPartiesNetworkErrorState by viewModel.showMyCreatedPartiesNetworkError.collectAsStateWithLifecycle()
    val isMyCreatedPartiesLoadingViewState by viewModel.isMyCreatedPartiesLoadingView.collectAsStateWithLifecycle()
    val isAllPartyListNetworkErrorState by viewModel.isAllPartyListNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showAllPartyListNetworkErrorState by viewModel.showAllPartyListNetworkError.collectAsStateWithLifecycle()
    val isAllPartyListLoadingViewState by viewModel.isAllPartyListLoadingView.collectAsStateWithLifecycle()
    val isEnterChatRoomState by viewModel.isEnterChatRoom.collectAsStateWithLifecycle(initialValue = false)
    val showEnterChatRoomState by viewModel.showEnterChatRoom.collectAsStateWithLifecycle()
    val isEnterRoomLoadingViewState by viewModel.isEnterRoomLoadingView.collectAsStateWithLifecycle()
    val isWithdrawMembershipNetworkErrorState by viewModel.isWithdrawMembershipNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showWithdrawMembershipNetworkErrorState by viewModel.showWithdrawMembershipNetworkError.collectAsStateWithLifecycle()
    val isWithdrawMembershipLoadingViewState by viewModel.isWithdrawMembershipLoadingView.collectAsStateWithLifecycle()
    val isDeleteChatRoomNetworkErrorState by viewModel.isDeleteChatRoomNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showDeleteChatRoomNetworkErrorState by viewModel.showDeleteChatRoomNetworkError.collectAsStateWithLifecycle()
    val isDeleteChatRoomLoadingViewState by viewModel.isDeleteChatRoomLoadingView.collectAsStateWithLifecycle()
    val isAllChatRoomListNetworkErrorState by viewModel.isAllChatRoomListNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showAllChatRoomListNetworkErrorState by viewModel.showAllChatRoomListNetworkError.collectAsStateWithLifecycle()
    val isAllChatRoomListLoadingViewState by viewModel.isAllChatRoomListLoadingView.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            item {
                UserProfileInfo(userInfo = viewModel.userInfo)
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                SettingButton(text = "로그아웃") {
                    viewModel.changeShowLogoutDialog(showDialog = true)
                }
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                SettingButton(text = "회원탈퇴") {
                    viewModel.changeShowWithdrawMembershipDialog(showDialog = true)
                }
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 8.dp
                    )
                ) {
                    Text(
                        text = "내가 ",
                        style = MaterialTheme.typography.h5,
                    )
                    Text(
                        text = "개설",
                        style = MaterialTheme.typography.h5,
                        color = Brown900,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "한 파티",
                        style = MaterialTheme.typography.h5,
                    )
                }
            }

            item {
                if (myCreatedPartiesState.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NoPartiesAvailable()
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        items(
                            items = myCreatedPartiesState,
                            key = { myParty -> myParty.postId }
                        ) { myParty ->
                            HomeDetailPartyContent(party = myParty,
                                chatRoomList = chatRoomItemListState,
                                onDetailInfoClick = {
                                    viewModel.onDetailInfoClick(
                                        party = myParty,
                                        navController = navController
                                    )
                                },
                                onChatJoinClick = {
                                    viewModel.enterChatRoom(
                                        party = myParty,
                                        chatRoomItemList = chatRoomItemListState,
                                        navController = navController
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 8.dp
                    )
                ) {
                    Text(
                        text = "내가 ",
                        style = MaterialTheme.typography.h5,
                    )
                    Text(
                        text = "참가",
                        style = MaterialTheme.typography.h5,
                        color = Brown900,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "한 파티",
                        style = MaterialTheme.typography.h5,
                    )
                }
            }

            item {
                if (joinedParties.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NoPartiesAvailable()
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        items(
                            items = joinedParties,
                            key = { myParty -> myParty.postId }
                        ) { joinedParty ->
                            HomeDetailPartyContent(party = joinedParty,
                                chatRoomList = chatRoomItemListState,
                                onDetailInfoClick = {
                                    viewModel.onDetailInfoClick(
                                        party = joinedParty,
                                        navController = navController
                                    )
                                },
                                onChatJoinClick = {
                                    viewModel.enterChatRoom(
                                        party = joinedParty,
                                        chatRoomItemList = chatRoomItemListState,
                                        navController = navController
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }
        }

        YesNoDialog(
            showDialog = showLogoutDialogState,
            onYesClick = { viewModel.logout(navController = navController) },
            onNoClick = { viewModel.changeShowLogoutDialog(showDialog = false) },
            title = "로그아웃을 하시겠습니까?",
            message = "로그아웃 됩니다.",
            confirmButtonMessage = "네",
            dismissButtonMessage = "아니오"
        )

        YesNoDialog(
            showDialog = showWithdrawMembershipDialogState,
            onYesClick = { viewModel.withdrawMembership(navController = navController) },
            onNoClick = { viewModel.changeShowWithdrawMembershipDialog(showDialog = false) },
            title = "회원탈퇴를 하시겠습니까?",
            message = "회원탈퇴 됩니다.",
            confirmButtonMessage = "네",
            dismissButtonMessage = "아니오"
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showMyCreatedPartiesNetworkErrorState,
            showMessage = isMyCreatedPartiesNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isMyCreatedPartiesLoadingViewState
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showAllPartyListNetworkErrorState,
            showMessage = isAllPartyListNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isAllPartyListLoadingViewState
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
            showToast = showWithdrawMembershipNetworkErrorState,
            showMessage = isWithdrawMembershipNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isWithdrawMembershipLoadingViewState
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
            isLoading = isDeleteChatRoomLoadingViewState
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showAllChatRoomListNetworkErrorState,
            showMessage = isAllChatRoomListNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isAllChatRoomListLoadingViewState
        )
    }
}

@Composable
fun UserProfileInfo(userInfo: LoginResponse?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = userInfo?.userImageUrl),
            contentDescription = "사용자 닉네임",
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 5.dp, top = 1.dp)
        ) {
            userInfo?.userNickname?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h4
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "님",
                color = SettingScreenColor,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun SettingButton(text: String, modifier: Modifier = Modifier.padding(24.dp), onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
