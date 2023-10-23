package com.mbj.doeat.ui.component.party

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.ui.theme.Color.Companion.Beige100
import com.mbj.doeat.ui.theme.Color.Companion.Gray200
import com.mbj.doeat.ui.theme.Color.Companion.Remon400

@Composable
fun HomeDetailPartyContent(
    party: Party,
    chatRoomList: List<ChatRoom>?,
    onDetailInfoClick: (() -> Unit)? = null,
    onChatJoinClick: () -> Unit
) {
    val chatRoom = chatRoomList?.find { it.postId == party.postId.toString() }
    val isPartyFull = chatRoom?.members?.count() == party.recruitmentLimit
    val currentMembers = chatRoom?.members?.size ?: 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Beige100, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
            .clickable {
                if (onDetailInfoClick != null) {
                    onDetailInfoClick()
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = party.restaurantName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = party.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = party.restaurantLocation,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)

            )

            Text(
                text = if (party.detail == "") {
                    "세부사항이 없습니다."
                } else {
                    party.detail
                },
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (party.detail == "") {
                    Gray200
                } else {
                    Color.Black
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${currentMembers} / ${party.recruitmentLimit}",
                    fontSize = 22.sp,
                    color = if (isPartyFull) {
                        Color.Red
                    } else {
                        Color.Black
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.22f)
                )

                Spacer(modifier = Modifier.width(40.dp))

                Text(
                    text = "참가하기",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .background(Remon400, shape = RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .clickable {
                            onChatJoinClick()
                        }
                )
            }
        }
    }
}
