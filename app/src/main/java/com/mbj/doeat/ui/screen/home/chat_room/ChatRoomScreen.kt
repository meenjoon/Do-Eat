package com.mbj.doeat.ui.screen.home.chat_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.ui.component.ChatRoomContent
import com.mbj.doeat.ui.screen.home.chat_room.viewmodel.ChatRoomViewModel
import com.mbj.doeat.ui.theme.Color.Companion.NormalColorInverted

@Composable
fun ChatRoomScreen(name: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: ChatRoomViewModel = hiltViewModel()

    val myChatRoomListState by viewModel.myChatRoomList.collectAsStateWithLifecycle()
    val userListState by viewModel.userList.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NormalColorInverted)
        ) {
            Text(
                text = "채팅 목록",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            )

            LazyColumn(
                Modifier
                    .fillMaxHeight(0.9f)
                    .padding(8.dp)
            ) {
                items(
                    items = myChatRoomListState!!,
                    key = { chatRoom -> chatRoom.postId.toString() }
                ) { chatRoom ->
                    val memberImages = viewModel.chatRoomImages(userListState, chatRoom.members)
                    ChatRoomContent(chatRoom = chatRoom, memberImages = memberImages) {
                        viewModel.enterChatRoom(
                            chatRoom = chatRoom,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
