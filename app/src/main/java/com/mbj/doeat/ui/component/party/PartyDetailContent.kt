package com.mbj.doeat.ui.component.party

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import com.mbj.doeat.ui.theme.Color.Companion.Gray200
import com.mbj.doeat.ui.theme.Color.Companion.NormalColor
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700

@Composable
fun PartyDetailContent(party: Party?, chatRoom: ChatRoom?) {
    val scrollState = rememberLazyListState()

    val isPartyFull = chatRoom?.members?.count() == party?.recruitmentLimit

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        party?.restaurantName?.let {
            Text(
                text = it,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (party != null) {
            Text(
                text = party.category,
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (party != null) {
            Text(
                text = party.restaurantLocation,
                style = MaterialTheme.typography.body1,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .border(1.dp, NormalColor, shape = RoundedCornerShape(8.dp))
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
        ) {
            item {
                if (party != null) {
                    Text(
                        text = if (party.detail == "") {
                            "세부사항이 없습니다."
                        } else {
                            party.detail
                        },
                        style = MaterialTheme.typography.body1,
                        color = if (party.detail == "") {
                            Gray200
                        } else {
                            NormalColor
                        },
                        letterSpacing = 2.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (party != null) {
            Text(
                text = "${chatRoom?.members?.count() ?: 1} / ${party.recruitmentLimit}",
                fontSize = 26.sp,
                color = if (isPartyFull) {
                    Color.Red
                } else {
                    Yellow700
                },
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}
