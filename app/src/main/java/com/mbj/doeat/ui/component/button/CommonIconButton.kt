package com.mbj.doeat.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.component.image.IconComponentImageVector
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700

@Composable
fun CommonIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Yellow700, CircleShape)
            .size(33.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        IconComponentImageVector(icon = imageVector, size = 15.dp, tint = Color.Black)
    }
}
