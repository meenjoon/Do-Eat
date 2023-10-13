package com.mbj.doeat.ui.component

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.ui.theme.Beige100
import com.mbj.doeat.ui.theme.Remon400

@Composable
fun PartyItem(
    party: Party,
    onJoinClick: (Party) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Beige100, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
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
                fontSize = 11.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)

            )

            Text(
                text = party.detail,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .clickable {
                        onJoinClick(party)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${party.currentNumberPeople} / ${party.recruitmentLimit}",
                    fontSize = 22.sp,
                    color = Color.Black,
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
                            /**
                             * 채팅방 참가하기 TODO
                             */
                            onJoinClick
                        }
                )
            }
        }
    }
}

@Composable
fun PartyList(
    viewModel: ViewModel,
    partyListState: List<Party>,
    modifier: Modifier,
    onClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = partyListState,
            key = { party -> party.postId }
        ) { party ->
            PartyItem(party = party, onJoinClick = {})
        }
    }
}
