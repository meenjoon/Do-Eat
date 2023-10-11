package com.mbj.doeat.ui.screen.home.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.mbj.doeat.BuildConfig
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.ui.component.LongRectangleButtonWithParams
import com.mbj.doeat.ui.screen.home.detail.viewmodel.DetailViewModel
import com.mbj.doeat.ui.theme.Beige100
import com.mbj.doeat.ui.theme.DoEatTheme
import com.mbj.doeat.ui.theme.Remon400
import com.mbj.doeat.util.UrlUtils

@Composable
fun DetailScreen(searchItem: SearchItem, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: DetailViewModel = hiltViewModel()

    viewModel.updateSearchItem(searchItem)
    val searchItemState by viewModel.searchItem.collectAsState()

    val webViewClient = AccompanistWebViewClient()
    val webChromeClient = AccompanistWebChromeClient()
    val webViewNavigator = rememberWebViewNavigator()
    val url = if (searchItemState?.link == "") {
        "${BuildConfig.NAVER_SEARCH_BASE_URL}search.naver?query=${searchItemState?.title}"
    } else {
        UrlUtils.decodeUrl(searchItemState?.link ?: "")
    }
    val webViewState = rememberWebViewState(
        url = url,
        additionalHttpHeaders = emptyMap()
    )

    val partyListState by viewModel.partyList.collectAsState()

    Scaffold(
        bottomBar = {
            LongRectangleButtonWithParams(
                text = "파티원 구하기",
                fontSize = 20.sp,
                height = 60.dp,
                useFillMaxWidth = true,
                padding = PaddingValues(top = 5.dp, bottom = 5.dp, start = 15.dp, end = 15.dp),
                backgroundColor = Remon400,
                contentColor = Color.Black,
                shape = RoundedCornerShape(12.dp)
            ) {
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.clickable {
                            if (webViewNavigator.canGoBack) {
                                webViewNavigator.navigateBack()
                            } else {
                                navController.popBackStack()
                            }
                        },
                    )
                }

                WebView(
                    state = webViewState,
                    navigator = webViewNavigator,
                    client = webViewClient,
                    chromeClient = webChromeClient,
                    onCreated = { webView ->
                        with(webView) {
                            settings.run {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                javaScriptCanOpenWindowsAutomatically = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp)
                )

                Text(
                    text = "현재 개설된 파티",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, bottom = 10.dp)
                )

                LazyColumn(
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    items(
                        items = partyListState,
                        key = { party -> party.postId }
                    ) { party ->
                        PartyItem(party = party, onJoinClick = {})
                    }
                }
            }
        }
    )
}

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
                        .fillMaxWidth(0.6f)
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
                    modifier = Modifier
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

@Preview
@Composable
fun PartyItemPreview() {
    DoEatTheme {
        val dummyParty = Party(
            postId = 11,
            userId = 4,
            restaurantName = "마노디셰프1231231212231231313131313123",
            restaurantLocation = "서울특별시 송파구 송파대로 570 타워 730 B1",
            recruitmentLimit = 10,
            currentNumberPeople = 5,
            detail = "This is a party description. This is a party description. This is a party description. This is a party description. This is a party description. This is a party description.",
            link = "",
            category = "음식점>이탈리아 음식 12313123131312312"
        )
        PartyItem(party = dummyParty, onJoinClick = {})
    }
}
