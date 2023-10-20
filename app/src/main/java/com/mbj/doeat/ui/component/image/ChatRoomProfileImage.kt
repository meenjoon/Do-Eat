package com.mbj.doeat.ui.component.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import androidx.compose.ui.graphics.Color.Companion.Transparent

@Composable
fun ChatRoomProfileImage(
    imageUrl: String,
    contentDescription: String,
    size: Dp
) {
    Image(
        painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
            }),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Transparent, CircleShape),
    )
}
