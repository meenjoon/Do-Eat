package com.mbj.doeat.ui.screen.home.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.ui.component.HomeDetailPartyContent
import com.mbj.doeat.ui.component.divider.HorizontalDivider
import com.mbj.doeat.ui.screen.home.setting.viewmodel.SettingViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700
import com.mbj.doeat.ui.theme.Color.Companion.SettingDividerColor

@Composable
fun SettingScreen(name: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: SettingViewModel = hiltViewModel()

    val userInfoState by viewModel.userInfo.collectAsStateWithLifecycle()
    val myCreatedPartiesState by viewModel.myCreatedParties.collectAsStateWithLifecycle()
    val joinedParties by viewModel.joinedParties.collectAsStateWithLifecycle()
    val chatRoomItemListState by viewModel.chatRoomItemList.collectAsStateWithLifecycle()


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            item {
                UserProfileInfo(userInfo = userInfoState)
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                SettingButton(text = "로그아웃") {

                }
            }

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }

            item {
                SettingButton(text = "회원탈퇴") {

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
                        color = Yellow700
                    )
                    Text(
                        text = "한 파티",
                        style = MaterialTheme.typography.h5,
                    )
                }
            }

            item {
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
                        color = Yellow700
                    )
                    Text(
                        text = "한 파티",
                        style = MaterialTheme.typography.h5,
                    )
                }
            }

            item {
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

            item {
                HorizontalDivider(color = SettingDividerColor, thickness = 1.dp)
            }
        }
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
                color = Yellow700,
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
