package com.mbj.doeat.ui.component.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mbj.doeat.ui.theme.Color.Companion.Black700

@Composable
fun LoadingView(
    isLoading: Boolean,
    boxColor: Color = Black700,
    circularProgressIndicatorColor: Color = Color.White
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(boxColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = circularProgressIndicatorColor)
        }
    }
}
