package com.mbj.doeat.ui.screen.home.chat_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.mbj.doeat.R
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.LoginResponse
import com.mbj.doeat.ui.component.ChatContent
import com.mbj.doeat.ui.component.button.BackButton
import com.mbj.doeat.ui.component.textfield.CustomTextField
import com.mbj.doeat.ui.screen.home.chat_detail.viewmodel.ChatDetailViewModel
import com.mbj.doeat.ui.theme.Color.Companion.ChatDetailBottomSheetColor
import com.mbj.doeat.util.Keyboard
import com.mbj.doeat.util.keyboardAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatDetailScreen(postId: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: ChatDetailViewModel = hiltViewModel()
    viewModel.updatePostId(postId)

    val sendMessageState by viewModel.sendMessage.collectAsStateWithLifecycle()
    val chatItemListState by viewModel.chatItemList.collectAsStateWithLifecycle()
    val chatRoomItemState by viewModel.chatRoomItem.collectAsStateWithLifecycle()
    val chatRoomMembersState by viewModel.chatRoomMembers.collectAsStateWithLifecycle()

    var previousChatItemList by remember { mutableStateOf(chatItemListState) }
    val listState = rememberLazyListState()
    val isKeyboardOpen by keyboardAsState()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(chatItemListState) {
        onDispose {
            previousChatItemList = chatItemListState
        }
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        color = ChatDetailBottomSheetColor,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .offset(y = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn {
                    items(chatRoomMembersState,
                        key = { chatRoomMember ->
                            chatRoomMember.userId!!
                        }) { chatRoomMember ->
                        ChatRoomMemberItem(
                            user = chatRoomMember,
                            chatRoom = chatRoomItemState
                        )
                    }
                }
            }
        },
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetPeekHeight = 25.dp,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        BackButton(
                            modifier = Modifier.padding(8.dp),
                            navController = navController
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        chatRoomItemState?.name?.let { name ->
                            Text(
                                text = name,
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.fillMaxWidth(0.8f),
                                textAlign = TextAlign.Center
                            )
                        }

                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "채팅방 나가기",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    viewModel.leaveChatRoom(navController)
                                })
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            top = 25.dp,
                            end = 15.dp,
                            bottom = 100.dp
                        )
                    ) {
                        items(chatItemListState, key = { message ->
                            message.chatId!!
                        }) { message ->
                            ChatContent(message, chatRoomItemState)
                        }

                        if (isKeyboardOpen == Keyboard.Opened || previousChatItemList.size != chatItemListState.size) {
                            coroutineScope.launch {
                                val targetIndex = maxOf(0, chatItemListState.size - 1)
                                listState.scrollToItem(targetIndex)
                            }
                        }
                    }
                    previousChatItemList = chatItemListState
                }

                CustomTextField(
                    text = sendMessageState,
                    trailingIconImageVector = Icons.Default.Send,
                    onValueChange = { newMessage ->
                        viewModel.changeSendMessage(newMessage)
                    },
                    onClick = {
                        viewModel.sendMessage(sendMessageState)
                        viewModel.changeSendMessage("")
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-15).dp)
                )
            }
        }
    )
}

@Composable
fun ChatRoomMemberItem(
    user: LoginResponse,
    chatRoom: ChatRoom?
) {
    val isMaster = chatRoom?.members?.any { (_, inMember) ->
        inMember.userId == user.userId.toString() && inMember.guest == false
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = user.userImageUrl),
            contentDescription = "사용자 닉네임",
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 5.dp, top = 1.dp)
        ) {
            Text(
                text = user.userNickname!!,
                style = MaterialTheme.typography.body1
            )

            if (isMaster == true) {
                Image(
                    painter = painterResource(id = R.drawable.crown_icon),
                    contentDescription = "방장",
                    modifier = Modifier
                        .size(width = 40.dp, height = 24.dp)
                )
            }
        }
    }
}

