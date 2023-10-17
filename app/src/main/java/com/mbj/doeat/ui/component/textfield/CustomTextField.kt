package com.mbj.doeat.ui.component.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mbj.doeat.ui.component.button.CommonIconButton

@Composable
fun CustomTextField(
    text: String,
    trailingIconImageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = "메세지를 입력해주세요.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            textColor = Color.Black
        ),
        trailingIcon = {
            CommonIconButton(imageVector = trailingIconImageVector) {
                onClick()
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape
    )
}
