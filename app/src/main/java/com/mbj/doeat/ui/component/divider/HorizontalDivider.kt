package com.mbj.doeat.ui.component.divider

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.theme.Color.Companion.Gray100

@Composable
fun HorizontalDivider(color: Color = Gray100, thickness: Dp = 1.dp, modifier: Modifier = Modifier) {
    Divider(
        color = color,
        thickness = thickness,
        modifier = modifier
    )
}
