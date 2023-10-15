package com.mbj.doeat.ui.screen.home.community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.ui.component.HomeDetailPartyContent
import com.mbj.doeat.ui.component.SearchAppBar
import com.mbj.doeat.ui.graph.DetailScreen
import com.mbj.doeat.ui.screen.home.community.viewModel.PostListViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Remon400
import com.mbj.doeat.util.MapConverter
import com.mbj.doeat.util.NavigationUtils
import com.mbj.doeat.util.UrlUtils

@Composable
fun PostListScreen(name: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: PostListViewModel = hiltViewModel()

    val partyListState by viewModel.partyList.collectAsStateWithLifecycle()
    val searchFilterTextState by viewModel.searchBarText.collectAsStateWithLifecycle()
    val filteredPartyList = viewModel.getFilteredPartyList(partyListState, searchFilterTextState)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(15.dp))

            SearchAppBar(
                text = searchFilterTextState,
                height = 70.dp,
                leftAndRightPaddingDp = 8.dp,
                backgroundColor = Remon400,
                contentColor = Color.Black,
                searchAppBarText = "파티를 검색해주세요.",
                roundedCornerShape = RoundedCornerShape(8.dp),
                onTextChange = { newText ->
                    viewModel.updateSearchBarText(newText)
                },
                onCloseClicked = {},
                onSearchClicked = {}
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                items(
                    items = filteredPartyList.sortedByDescending { it.postId },
                    key = { party -> party.postId }
                ) { party ->
                    HomeDetailPartyContent(party = party,
                        onDetailInfoClick = {
                            val encodedLink = UrlUtils.encodeUrl(party.link)
                            val titleWithoutHtmlTags = MapConverter.removeHtmlTags(party.restaurantName)
                            NavigationUtils.navigate(
                                navController, DetailScreen.DetailWriter.navigateWithArg(
                                    party.copy(
                                        restaurantName = titleWithoutHtmlTags,
                                        link = encodedLink
                                    )
                                )
                            )
                        },
                        onChatJoinClick = {})
                }
            }
        }
    }
}
