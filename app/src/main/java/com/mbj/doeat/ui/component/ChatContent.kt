package com.mbj.doeat.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.mbj.doeat.R
import com.mbj.doeat.data.remote.model.ChatItem
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.ui.theme.Color.Companion.LightRed
import com.mbj.doeat.ui.theme.Color.Companion.LightYellow
import com.mbj.doeat.util.DateUtils.getFormattedElapsedTime
import com.mbj.doeat.util.UserDataStore

@Composable
fun ChatContent(
    chat: ChatItem,
    chatRoom: ChatRoom?
) {

    val myId = UserDataStore.getLoginResponse()?.userId
    val isOther = chat.userId != myId
    val isMaster = chatRoom?.members?.any { (key, value) -> value == "master" && key == chat.userId.toString() } == true

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 5.dp, top = 1.dp)
                ) {
                    Text(
                        text = chat.nickname!!,
                        style = TextStyle(
                            color = Gray,
                            fontSize = 14.sp
                        ),
                    )

                    if (isMaster) {
                        Image(
                            painter = painterResource(id = R.drawable.crown_icon),
                            contentDescription = "방장",
                            modifier = Modifier
                                .size(width = 40.dp, height = 24.dp) // 왕관 아이콘의 크기를 조정
                        )
                    }
                }
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
                text = getFormattedElapsedTime(chat.lastSentTime!!),
                style = TextStyle(
                    color = Gray,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
            )
        }
    }
}
