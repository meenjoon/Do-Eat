package com.mbj.doeat.ui.screen.home.chat_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.ui.component.ChatContent
import com.mbj.doeat.ui.component.button.BackButton
import com.mbj.doeat.ui.component.textfield.CustomTextField
import com.mbj.doeat.ui.screen.home.chat_detail.viewmodel.ChatDetailViewModel

@Composable
fun ChatDetailScreen(postId: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: ChatDetailViewModel = hiltViewModel()
    viewModel.updatePostId(postId)

    val sendMessageState by viewModel.sendMessage.collectAsStateWithLifecycle()
    val chatItemListState by viewModel.chatItemList.collectAsStateWithLifecycle()
    val chatRoomItemState by viewModel.chatRoomItem.collectAsStateWithLifecycle()

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
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.padding(
                    start = 15.dp,
                    top = 25.dp,
                    end = 15.dp,
                    bottom = 75.dp
                )
            ) {
                items(chatItemListState) { message ->
                    ChatContent(message, chatRoomItemState)
                }
            }
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
        )
    }
}
