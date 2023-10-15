package com.mbj.doeat.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val DoEatTypography = Typography()

val Typography.button1: TextStyle
    @Composable get() = button.copy(
        fontSize = 20.sp,
    )
