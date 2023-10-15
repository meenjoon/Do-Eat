package com.mbj.doeat.ui.screen.home.detail.detail_writer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mbj.doeat.data.remote.model.Party
import com.mbj.doeat.ui.component.BackButton
import com.mbj.doeat.ui.component.LoadingView
import com.mbj.doeat.ui.component.LongRectangleButtonWithParams
import com.mbj.doeat.ui.component.PartyDetailItem
import com.mbj.doeat.ui.component.ReusableWebView
import com.mbj.doeat.ui.component.YesNoDialog
import com.mbj.doeat.ui.screen.home.detail.detail_writer.viewmodel.DetailWriterViewModel
import com.mbj.doeat.ui.theme.Color.Companion.Red500
import com.mbj.doeat.ui.theme.button1

@Composable
fun DetailWriterScreen(party: Party, navController: NavHostController, onClick: () -> Unit) {

    val viewModel: DetailWriterViewModel = hiltViewModel()
    viewModel.updateSearchItem(party)

    val partyItemState by viewModel.partyItem.collectAsStateWithLifecycle()
    val showCreatePartyDialogState by viewModel.showDeletePartyDialog.collectAsStateWithLifecycle()
    val isLoadingView by viewModel.isLoadingView.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            LongRectangleButtonWithParams(
                text = "삭제하기",
                height = 60.dp,
                useFillMaxWidth = true,
                padding = PaddingValues(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
                backgroundColor = Red500,
                contentColor = Color.White,
                textStyle = MaterialTheme.typography.button1
            ) {
                viewModel.changeShowDeletePartyDialog(showDialog = true)
            }
        }
    ) { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(navController)
                }

                ReusableWebView(
                    url = partyItemState?.link,
                    restaurantName = partyItemState?.restaurantName!!,
                    webViewModifier = Modifier.fillMaxHeight(0.5f),
                ) {}

                PartyDetailItem(partyItemState!!)
            }

            YesNoDialog(
                showDialog = showCreatePartyDialogState,
                onYesClick = { viewModel.deleteParty(navController) },
                onNoClick = { viewModel.changeShowDeletePartyDialog(showDialog = false) },
                title = "파티를 삭제 하시겠습니까?",
                message = "파티가 삭제됩니다.",
                confirmButtonMessage = "삭제",
                dismissButtonMessage = "취소"
            )

            LoadingView(
                isLoading = isLoadingView,
            )
        }
    }
}
