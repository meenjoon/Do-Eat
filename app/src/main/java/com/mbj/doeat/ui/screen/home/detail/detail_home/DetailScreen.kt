package com.mbj.doeat.ui.screen.home.detail.detail_home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.ChatRoom
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.data.remote.model.SearchItem
import com.mbj.doeat.ui.component.button.BackButton
import com.mbj.doeat.ui.component.loading.LoadingView
import com.mbj.doeat.ui.component.button.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.HomeDetailPartyContent
import com.mbj.doeat.ui.component.ReusableWebView
import com.mbj.doeat.ui.component.toast.ToastMessage
import com.mbj.doeat.ui.component.dialog.YesNoDialog
import com.mbj.doeat.ui.screen.home.detail.detail_home.viewmodel.DetailViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Gray200
import com.mbj.doeat.ui.theme.Color.Companion.NormalColor
import com.mbj.doeat.ui.theme.Color.Companion.NormalColorInverted
import com.mbj.doeat.ui.theme.Color.Companion.Remon400

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(searchItem: SearchItem, navController: NavHostController, onClick: () -> Unit) {
    val viewModel: DetailViewModel = hiltViewModel()
    viewModel.updateSearchItem(searchItem)

    val searchItemState by viewModel.searchItem.collectAsStateWithLifecycle()
    val partyListState by viewModel.partyList.collectAsStateWithLifecycle()
    val recruitmentCountState by viewModel.recruitmentCount.collectAsStateWithLifecycle()
    val recruitmentDetailsState by viewModel.recruitmentDetails.collectAsStateWithLifecycle()
    val isBottomSheetExpandedState by viewModel.isBottomSheetExpanded.collectAsStateWithLifecycle()
    val showCreatePartyDialogState by viewModel.showCreatePartyDialog.collectAsStateWithLifecycle()
    val chatRoomItemListState by viewModel.chatRoomItemList.collectAsStateWithLifecycle()

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
                        color = NormalColorInverted,
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

            chatRoomItemListState?.let {
                DetailContent(
                    viewModel = viewModel,
                    searchItem = searchItemState!!,
                    navController = navController,
                    partyListState = partyListState,
                    chatRoomItemList = it,
                    onClick = onClick,
                    padding = padding
                )
            }

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
fun DetailContent(
    viewModel: DetailViewModel,
    searchItem: SearchItem,
    navController: NavHostController,
    partyListState: List<Party>,
    chatRoomItemList: List<ChatRoom>,
    onClick: () -> Unit,
    padding: PaddingValues
) {
    val isPostLoadingViewState by viewModel.isPostLoadingView.collectAsStateWithLifecycle()
    val showValidRecruitmentCountState by viewModel.showValidRecruitmentCount.collectAsStateWithLifecycle()
    val showEnterChatRoomState by viewModel.showEnterChatRoom.collectAsStateWithLifecycle()
    val isEnterChatRoomState by viewModel.isEnterChatRoom.collectAsStateWithLifecycle(initialValue = false)
    val isValidRecruitmentCountState by viewModel.isValidRecruitmentCount.collectAsStateWithLifecycle(initialValue = false)

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
                BackButton(navController = navController)
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp)
            ) {
                ReusableWebView(
                    url = searchItem.link,
                    restaurantName = searchItem.title,
                    webViewModifier = Modifier.fillMaxSize(),
                ) {
                }

                ToastMessage(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    showToast = showValidRecruitmentCountState,
                    showMessage = isValidRecruitmentCountState,
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
                partyListState = partyListState,
                chatRoomItemList = chatRoomItemList,
                navController = navController
            ) {
            }

            CreatePartyButton(onClick = {
                viewModel.toggleBottomSheetState()
            })
        }

        LoadingView(
            isLoading = isPostLoadingViewState,
        )

        ToastMessage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            showToast = showEnterChatRoomState,
            showMessage = isEnterChatRoomState,
            message = "현재 인원이 꽉 찼습니다."
        )
    }
}

@Composable
fun PartiesSection(viewModel: DetailViewModel, partyListState: List<Party>, chatRoomItemList: List<ChatRoom>, navController: NavHostController , onClick: () -> Unit) {
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
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = partyListState.sortedByDescending { it.postId },
                        key = { party -> party.postId }
                    ) { party ->
                        HomeDetailPartyContent(party = party,
                            chatRoomList = chatRoomItemList,
                            onDetailInfoClick = {
                                viewModel.onDetailInfoClick(
                                    party = party,
                                    navController = navController
                                )
                            },
                            onChatJoinClick = {
                                viewModel.enterChatRoom(
                                    party = party,
                                    chatRoomItemList = chatRoomItemList,
                                    navController = navController
                                )
                            })
                    }
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
        )

        Text(
            text = party?.title ?: "제목이 제공되지 않습니다.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(
            text = "장소",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
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
                NormalColor
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
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = NormalColor,
                errorBorderColor = Color.Red,
                focusedBorderColor = NormalColor,
                unfocusedBorderColor = if (recruitmentCount.isEmpty()) Color.Red else NormalColor,
                textColor = NormalColor
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
                focusedBorderColor = NormalColor,
                unfocusedBorderColor = NormalColor,
                errorLabelColor = Color.Red,
                unfocusedLabelColor = NormalColor,
                focusedLabelColor = NormalColor,
                cursorColor = NormalColor,
                textColor = NormalColor
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
