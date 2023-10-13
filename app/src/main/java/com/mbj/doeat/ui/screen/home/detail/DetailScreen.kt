package com.mbj.doeat.ui.screen.home.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.mbj.doeat.BuildConfig
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.ui.component.LoadingView
import com.mbj.doeat.ui.component.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.PartyList
import com.mbj.doeat.ui.component.ToastMessage
import com.mbj.doeat.ui.component.YesNoDialog
import com.mbj.doeat.ui.screen.home.detail.viewmodel.DetailViewModel
import com.mbj.doeat.ui.theme.Gray200
import com.mbj.doeat.ui.theme.Remon400
import com.mbj.doeat.ui.theme.Yellow700
import com.mbj.doeat.util.UrlUtils

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(searchItem: SearchItem, navController: NavHostController, onClick: () -> Unit) {
    val viewModel: DetailViewModel = hiltViewModel()
    viewModel.updateSearchItem(searchItem)

    val searchItemState by viewModel.searchItem.collectAsState()
    val partyListState by viewModel.partyList.collectAsState()
    val recruitmentCountState by viewModel.recruitmentCount.collectAsState()
    val recruitmentDetailsState by viewModel.recruitmentDetails.collectAsState()
    val isBottomSheetExpandedState by viewModel.isBottomSheetExpanded.collectAsState()
    val showCreatePartyDialogState by viewModel.showCreatePartyDialog.collectAsState()

    val webViewClient = AccompanistWebViewClient()
    val webChromeClient = AccompanistWebChromeClient()
    val webViewState = initializeWebView(searchItemState)
    val webViewNavigator = rememberWebViewNavigator()

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(650.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .offset(y = 15.dp)
            ) {
                DetailBottomSheet(
                    viewModel = viewModel,
                    party = searchItemState,
                    recruitmentCount = recruitmentCountState,
                    recruitmentDetails = recruitmentDetailsState,
                    onRecruitmentCountChange = { newCount ->
                        viewModel.changeRecruitmentCount(newCount)
                    }
                ) { newDetails ->
                    viewModel.changeRecruitmentDetails(newDetails)
                }
            }
        },
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetPeekHeight = 0.dp,
        content = { padding ->
            ExpandBottomSheetIfRequired(bottomSheetState, isBottomSheetExpandedState)

            DetailContent(
                viewModel = viewModel,
                navController = navController,
                webViewClient = webViewClient,
                webChromeClient = webChromeClient,
                webViewState = webViewState,
                webViewNavigator = webViewNavigator,
                partyListState = partyListState,
                onClick = onClick,
                padding = padding
            )

            YesNoDialog(
                showDialog = showCreatePartyDialogState,
                onYesClick = { viewModel.postParty(navHostController = navController) },
                onNoClick = { viewModel.changeShowCreatePartyDialog(showDialog = false) },
                title = "파티를 모집하시겠습니까?",
                message = "파티가 등록됩니다.",
                confirmButtonMessage = "등록",
                dismissButtonMessage = "취소"
            )
        }
    )
}

@Composable
fun initializeWebView(searchItemState: SearchItem?) = rememberWebViewState(
    url = getUrl(searchItemState),
    additionalHttpHeaders = emptyMap()
)

fun getUrl(searchItemState: SearchItem?): String {
    return if (searchItemState?.link == "") {
        "${BuildConfig.NAVER_SEARCH_BASE_URL}search.naver?query=${searchItemState?.title}"
    } else {
        UrlUtils.decodeUrl(searchItemState?.link ?: "")
    }
}

@Composable
fun DetailContent(
    viewModel: DetailViewModel,
    navController: NavHostController,
    webViewClient: AccompanistWebViewClient,
    webChromeClient: AccompanistWebChromeClient,
    webViewState: WebViewState,
    webViewNavigator: WebViewNavigator,
    partyListState: List<Party>,
    onClick: () -> Unit,
    padding: PaddingValues
) {
    val isPostLoadingViewState by viewModel.isPostLoadingView.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                BackButton(navController)
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp)
            ) {
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
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = {
                        if (webViewNavigator.canGoBack) {
                            webViewNavigator.navigateBack()
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = Yellow700
                    )
                }

                ToastMessage(
                    modifier = Modifier.padding(16.dp).align(Alignment.TopCenter),
                    showToast = viewModel.showValidRecruitmentCount.collectAsState().value,
                    showMessage = viewModel.isValidRecruitmentCount.collectAsState(initial = false).value,
                    message = "모집인원을 입력해주세요."
                )
            }

            Text(
                text = "현재 개설된 파티",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 10.dp)
            )

            PartiesSection(
                viewModel = viewModel,
                partyListState = partyListState
            ) {
            }

            CreatePartyButton(onClick = {
                viewModel.toggleBottomSheetState()
            })
        }

        LoadingView(
            isLoading = isPostLoadingViewState,
        )
    }
}

@Composable
fun BackButton(navController: NavHostController) {
    Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "뒤로 가기",
        modifier = Modifier.clickable {
            navController.popBackStack()
        }
    )
}

@Composable
fun PartiesSection(viewModel: DetailViewModel, partyListState: List<Party>, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (partyListState.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NoPartiesAvailable()
                }
            } else {
                PartyList(
                    viewModel = viewModel, partyListState = partyListState,
                    modifier = Modifier.weight(1f)
                ) {
                }
            }
            CreatePartyButton(onClick = {
                viewModel.toggleBottomSheetState()
            })
        }
    }
}

@Composable
fun NoPartiesAvailable() {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "No parties available",
        modifier = Modifier.size(120.dp),
        tint = Color.Red
    )
    Text(
        text = "현재 개설된 파티가 없습니다.",
        fontSize = 20.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun CreatePartyButton(onClick: () -> Unit) {
    LongRectangleButtonWithParams(
        text = "파티원 구하기",
        fontSize = 25.sp,
        height = 60.dp,
        useFillMaxWidth = true,
        padding = PaddingValues(
            top = 8.dp,
            bottom = 8.dp,
            start = 15.dp,
            end = 15.dp
        ),
        backgroundColor = Remon400,
        contentColor = Color.Black,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    )
}

@Composable
fun DetailBottomSheet(
    viewModel: DetailViewModel,
    party: SearchItem?,
    recruitmentCount: String,
    recruitmentDetails: String,
    onRecruitmentCountChange: (String) -> Unit,
    onRecruitmentDetailsChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "같이 갈 사람 구하기",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(
            text = "같이 갈 사람이 없다면 직접 \n만들어보세요",
            fontSize = 18.sp,
            color = Gray200
        )

        Spacer(modifier = Modifier.padding(top = 20.dp))

        Text(
            text = "맛집",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = party?.title ?: "제목이 제공되지 않습니다.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(
            text = "장소",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = party?.roadAddress ?: "주소가 제공되지 않습니다.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(
            text = "모집 인원*",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (recruitmentCount.isEmpty()) {
                Color.Red
            } else {
                Color.Black
            }

        )

        Spacer(modifier = Modifier.padding(top = 5.dp))

        OutlinedTextField(
            value = recruitmentCount,
            onValueChange = { newValue ->
                if (newValue.isDigitsOnly()) {
                    onRecruitmentCountChange(newValue)
                }
            },
            textStyle = TextStyle(fontSize = 17.sp),
            label = { Text("모집 인원") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                errorLabelColor = Color.Red,
                cursorColor = Color.Black,
                unfocusedLabelColor = if (recruitmentCount.isEmpty()) Color.Red else Color.Black,
                focusedLabelColor = Color.Black,
                errorBorderColor = Color.Red,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = if (recruitmentCount.isEmpty()) Color.Red else Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            isError = recruitmentCount.isEmpty()
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(
            text = "세부사항",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(top = 5.dp))

        OutlinedTextField(
            value = recruitmentDetails,
            onValueChange = { newValue ->
                onRecruitmentDetailsChange(newValue)
            },
            textStyle = TextStyle(fontSize = 17.sp),
            label = { Text("모집 세부사항") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                errorLabelColor = Color.Red,
                unfocusedLabelColor = Color.Black,
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        LongRectangleButtonWithParams(
            text = "파티 등록",
            fontSize = 25.sp,
            height = 60.dp,
            useFillMaxWidth = true,
            padding = PaddingValues(
                top = 8.dp,
                bottom = 8.dp,
                start = 15.dp,
                end = 15.dp
            ),
            backgroundColor = Remon400,
            contentColor = Color.Black,
            shape = RoundedCornerShape(12.dp)
        ) {
            viewModel.changeShowCreatePartyDialog(showDialog = true)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandBottomSheetIfRequired(
    bottomSheetState: BottomSheetState,
    toggle: Boolean,
) {
    LaunchedEffect(toggle) {
        bottomSheetState.expand()
    }
}
