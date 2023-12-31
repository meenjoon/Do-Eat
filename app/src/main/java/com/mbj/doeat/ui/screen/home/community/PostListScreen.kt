package com.mbj.doeat.ui.screen.home.community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.ui.component.loading.LoadingView
import com.mbj.doeat.ui.component.party.HomeDetailPartyContent
import com.mbj.doeat.ui.component.searchbar.SearchAppBar
import com.mbj.doeat.ui.component.toast.ToastMessage
import com.mbj.doeat.ui.screen.home.community.viewModel.PostListViewModel
import com.mbj.doeat.ui.theme.Color.Companion.NormalButtonColor
import com.mbj.doeat.ui.theme.Color.Companion.NormalColor

@Composable
fun PostListScreen(name: String, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: PostListViewModel = hiltViewModel()

    val partyListState by viewModel.partyList.collectAsStateWithLifecycle()
    val searchFilterTextState by viewModel.searchBarText.collectAsStateWithLifecycle()
    val filteredPartyList = viewModel.getFilteredPartyList(partyListState, searchFilterTextState)
    val chatRoomItemListState by viewModel.chatRoomItemList.collectAsStateWithLifecycle()
    val showEnterChatRoomState by viewModel.showEnterChatRoom.collectAsStateWithLifecycle()
    val isEnterChatRoomState by viewModel.isEnterChatRoom.collectAsStateWithLifecycle(initialValue = false)
    val isPartyListNetworkErrorState by viewModel.isPartyListNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showPartyListNetworkErrorState by viewModel.showPartyListNetworkError.collectAsStateWithLifecycle()
    val isPartyListLoadingViewState by viewModel.isPartyListLoadingView.collectAsStateWithLifecycle()
    val isChatRoomListNetworkErrorState by viewModel.isChatRoomListNetworkError.collectAsStateWithLifecycle(initialValue = false)
    val showChatRoomListNetworkErrorState by viewModel.showChatRoomListNetworkError.collectAsStateWithLifecycle()
    val isChatRoomListLoadingViewState by viewModel.isChatRoomListLoadingView.collectAsStateWithLifecycle()
    val enterRoomErrorMessageState by viewModel.enterRoomErrorMessage.collectAsStateWithLifecycle()
    val isEnterRoomLoadingViewState by viewModel.isEnterRoomLoadingView.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(15.dp))

            SearchAppBar(
                text = searchFilterTextState,
                height = 70.dp,
                leftAndRightPaddingDp = 8.dp,
                backgroundColor = NormalButtonColor,
                contentColor = NormalColor,
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
                    HomeDetailPartyContent(
                        party = party,
                        chatRoomList = chatRoomItemListState,
                        onDetailInfoClick = {
                            viewModel.onDetailInfoClick(
                                party = party,
                                navController = navController
                            )
                        },
                        onChatJoinClick = {
                            viewModel.enterChatRoom(
                                party = party,
                                chatRoomItemList = chatRoomItemListState,
                                navController = navController
                            )
                        })
                }
            }
        }

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showEnterChatRoomState,
            showMessage = isEnterChatRoomState,
            message = enterRoomErrorMessageState
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showPartyListNetworkErrorState,
            showMessage = isPartyListNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isPartyListLoadingViewState
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showChatRoomListNetworkErrorState,
            showMessage = isChatRoomListNetworkErrorState,
            message = "네트워크 연결을 다시 확인해주세요"
        )

        LoadingView(
            isLoading = isChatRoomListLoadingViewState
        )

        LoadingView(
            isLoading = isEnterRoomLoadingViewState
        )
    }
}
