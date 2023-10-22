package com.mbj.doeat.ui.component.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun ClickableImage(
    painter: Painter,
    contentDescription: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .clickable { onClick() }
    )
}
