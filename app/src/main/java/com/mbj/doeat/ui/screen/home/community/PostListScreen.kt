package com.mbj.doeat.ui.screen.home.community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.component.PartyList
import com.mbj.doeat.ui.component.SearchAppBar
import com.mbj.doeat.ui.screen.home.community.viewModel.PostListViewModel
import com.mbj.doeat.ui.theme.Remon400

@Composable
fun PostListScreen(name: String, onClick: () -> Unit) {

    val viewModel = PostListViewModel()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(15.dp))

            SearchAppBar(
                text = "",
                height = 70.dp,
                leftAndRightPaddingDp = 8.dp,
                backgroundColor = Remon400,
                contentColor = Color.Black,
                searchAppBarText = "파티를 검색해주세요.",
                roundedCornerShape = RoundedCornerShape(8.dp),
                onTextChange = {},
                onCloseClicked = {},
                onSearchClicked = {}
            )

            Spacer(modifier = Modifier.height(15.dp))

            PartyList(
                viewModel = viewModel, partyListState = emptyList(),
                modifier = Modifier.weight(1f)
            ) {
            }
        }
    }
}
