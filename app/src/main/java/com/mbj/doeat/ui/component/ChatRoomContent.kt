package com.mbj.doeat.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.ui.component.image.ChatRoomProfileImage
import com.mbj.doeat.ui.theme.Color.Companion.Gray200
import com.mbj.doeat.ui.theme.Color.Companion.Gray50
import com.mbj.doeat.util.DateUtils.formatCustomDate

@Composable
fun ChatRoomContent(
    chatRoom: ChatRoom?,
    memberImages: List<String>?,
    onChatJoinClick: () -> Unit
) {
    val currentMembers = chatRoom?.members?.size ?: 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(90.dp)
            .clickable {
                       onChatJoinClick()
            },
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Box(
               modifier = Modifier.fillMaxHeight(1f)
            ) {
                if (!memberImages.isNullOrEmpty()) {
                    when (memberImages.size) {
                        1 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.2f)
                                    .fillMaxHeight(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatRoomProfileImage(
                                    imageUrl = memberImages[0],
                                    contentDescription = "Profile Image 1",
                                    size = 48.dp
                                )
                            }
                        }

                        2 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.2f)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatRoomProfileImage(
                                    imageUrl = memberImages[0],
                                    contentDescription = "Profile Image 1",
                                    size = 30.dp
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                ChatRoomProfileImage(
                                    imageUrl = memberImages[1],
                                    contentDescription = "Profile Image 2",
                                    size = 30.dp
                                )
                            }
                        }

                        3 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[0],
                                        contentDescription = "Profile Image 1",
                                        size = 25.dp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[1],
                                        contentDescription = "Profile Image 2",
                                        size = 25.dp
                                    )

                                    Spacer(modifier = Modifier.width(3.dp))

                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[2],
                                        contentDescription = "Profile Image 3",
                                        size = 25.dp
                                    )
                                }
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[0],
                                        contentDescription = "Profile Image 1",
                                        size = 25.dp
                                    )

                                    Spacer(modifier = Modifier.width(3.dp))

                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[1],
                                        contentDescription = "Profile Image 2",
                                        size = 25.dp
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[2],
                                        contentDescription = "Profile Image 3",
                                        size = 25.dp
                                    )

                                    Spacer(modifier = Modifier.width(3.dp))

                                    ChatRoomProfileImage(
                                        imageUrl = memberImages[3],
                                        contentDescription = "Profile Image 4",
                                        size = 25.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(0.60f),
                ) {
                    chatRoom?.name?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = if (currentMembers <= 1) {
                            ""
                        } else {
                            "$currentMembers"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Gray50
                    )
                }

                chatRoom?.lastMessage?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Gray200
                    )
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth(0.1f))

            if (chatRoom?.lastMessageDate == null) {
                Text(
                    text = formatCustomDate(chatRoom?.createdChatRoomDate!!),
                    fontSize = 14.sp,
                    color = Gray200
                )
            } else {
                Text(
                    text = formatCustomDate(chatRoom.lastMessageDate),
                    fontSize = 14.sp,
                    color = Gray200
                )
            }
        }
    }
}
