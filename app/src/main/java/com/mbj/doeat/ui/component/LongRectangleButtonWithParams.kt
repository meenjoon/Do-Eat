package com.mbj.doeat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.theme.Yellow700
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp


@Composable
fun LongRectangleButtonWithParams(
    text: String,
    width: Dp = 0.dp,
    height: Dp,
    useFillMaxWidth: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Yellow700,
    contentColor: Color = Color.Black,
    textStyle: TextStyle = MaterialTheme.typography.button,
    onClick: () -> Unit
) {
    Box(

        modifier = Modifier
            .padding(padding)
            .then(if (useFillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(width))
            .height(height)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, backgroundColor, shape)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = backgroundColor,
                shape = shape
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = textStyle,
        )
    }
}
