package com.mbj.doeat.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mbj.doeat.ui.theme.Color.Companion.DialogColor
import com.mbj.doeat.ui.theme.Color.Companion.NegativeButtonColor
import com.mbj.doeat.ui.theme.Color.Companion.NormalColor
import com.mbj.doeat.ui.theme.Color.Companion.PositiveButtonColor
import com.mbj.doeat.ui.theme.DoEatTheme


@Composable
fun YesNoDialog(
    showDialog: Boolean,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmButtonMessage: String,
    dismissButtonMessage: String,
) {

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onNoClick() },
            modifier = modifier,
            title = {
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    color = Color.Black,
                    fontSize = 16.sp,
                )
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        onClick = {
                            onYesClick()
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .height(50.dp)
                            .width(90.dp)
                            .background(PositiveButtonColor, shape = RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = confirmButtonMessage,
                            color = NormalColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = {
                                  onNoClick()
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .height(50.dp)
                            .width(90.dp)
                            .background(NegativeButtonColor, shape = RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = dismissButtonMessage,
                            color = NormalColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = DialogColor
        )
    }
}

@Preview
@Composable
fun YesNoDialogPreview() {
    DoEatTheme {
        YesNoDialog(
            showDialog = true,
            onYesClick = {},
            onNoClick = {},
            title = "파티를 모집하시겠습니까?",
            message = "파티가 등록됩니다.",
            confirmButtonMessage = "등록",
            dismissButtonMessage = "취소"
        )
    }
}
