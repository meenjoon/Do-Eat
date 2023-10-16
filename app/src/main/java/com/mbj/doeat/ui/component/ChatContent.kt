package com.mbj.doeat.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.ui.component.textfield.CustomTextField
import com.mbj.doeat.ui.theme.Color.Companion.LightRed
import com.mbj.doeat.ui.theme.Color.Companion.LightYellow
import com.mbj.doeat.ui.theme.DoEatTheme
import com.mbj.doeat.util.UserDataStore

@Composable
fun ChatContent(
    chat: ChatItem
) {

    val myId = UserDataStore.getLoginResponse()?.userId
    val isOther = chat.userId != myId

    val productPainter = rememberImagePainter(
        data = chat.profileImage,
        builder = {
            crossfade(true)
        }
    )

    Row(
        modifier = Modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isOther) {
            Image(
                painter = productPainter,
                contentDescription = "상대방 프로필 이미지",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp),
            horizontalAlignment = if (isOther) Alignment.Start else Alignment.End
        ) {
            if (isOther) {
                Text(
                    text = chat.nickname!!,
                    style = TextStyle(
                        color = Gray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(bottom = 5.dp, top = 1.dp),
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (isOther) LightRed else LightYellow,
                        RoundedCornerShape(100.dp)
                    )
                    .padding(end = 3.dp),
                contentAlignment = Center
            ) {
                Text(
                    text = chat.message!!, style = TextStyle(
                        color = Black,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                    textAlign = TextAlign.End
                )
            }

            Text(
                text = chat.lastSentTime!!,
                style = TextStyle(
                    color = Gray,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
            )
        }
    }
}

@Preview()
@Composable
fun ChatRowPreview() {
    DoEatTheme() {
        var message = ""
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            LazyColumn(
                modifier = Modifier.padding(
                    start = 15.dp,
                    top = 25.dp,
                    end = 15.dp,
                    bottom = 75.dp
                )
            ) {
                items(chatItems) { message ->
                    ChatContent(message)
                }
            }

            CustomTextField(
                text = message, onValueChange = { message = it },
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .align(BottomCenter)
            )
        }
    }
}

val chatItems = listOf(
    ChatItem(
        chatId = "1",
        userId = 1L,
        message = "안녕하세요! 안녕하세요! 안녕하세요! 안녕하세요! 안녕하세요!",
        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9JPSZcpHIHEFGLKzPVa9rUqQAEHrcQvkTEHwkcr49&s",
        nickname = "유저1",
        lastSentTime = "2023-10-20 10:15"
    ),
    ChatItem(
        chatId = "2",
        userId = 5L,
        message = "안녕하세요, 반가워요! 안녕하세요, 반가워요! 안녕하세요, 반가워요! 안녕하세요, 반가워요!",
        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9JPSZcpHIHEFGLKzPVa9rUqQAEHrcQvkTEHwkcr49&s",
        nickname = "유저2",
        lastSentTime = "2023-10-20 10:20"
    ),
    ChatItem(
        chatId = "3",
        userId = 1L,
        message = "날씨가 좋네요!",
        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9JPSZcpHIHEFGLKzPVa9rUqQAEHrcQvkTEHwkcr49&s",
        nickname = "유저1",
        lastSentTime = "2023-10-20 10:25"
    ),
    ChatItem(
        chatId = "3",
        userId = 1L,
        message = "뭐하고 있나요~?",
        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9JPSZcpHIHEFGLKzPVa9rUqQAEHrcQvkTEHwkcr49&s",
        nickname = "유저1",
        lastSentTime = "2023-10-20 10:25"
    ),
    ChatItem(
        chatId = "4",
        userId = 5L,
        message = "맞아요, 기분이 좋아지네요!",
        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9JPSZcpHIHEFGLKzPVa9rUqQAEHrcQvkTEHwkcr49&s",
        nickname = "유저2",
        lastSentTime = "2023-10-20 10:30"
    )
)
