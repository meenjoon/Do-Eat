package com.mbj.doeat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class Color {
    companion object {
        private val darkTheme: Boolean
            @Composable
            get() = isSystemInDarkTheme()

        val DeepOceanBlue = Color(0xFF242B36)
        val Yellow700 = Color(0xFFFFEB23)
        val Gray50 = Color(0x94696A6F)
        val Gray100 = Color(0x65E9ECF7)
        val Gray200 = Color(0xFFB9B6B6)
        val Gray250 = Color(0xFF9E9D9D)
        val CharcoalGray = Color(0xFF303134)
        val Gray300 = Color(0x7C3A3636)
        val Beige50 = Color(0xFFFFF1F1)
        val Beige100 = Color(0xFFEBDFDF)
        val Beige150 = Color(0xFFFCF7E7)
        val Lemon400 = Color(0xFFF4FF7F)
        val Pink500 = Color(0xFFFF7F7F)
        val Red500 = Color(0xFFFB3F16)
        val Black700 = Color(0x77000000)
        val LightRed = Color(0XFFFFE1CC)
        val LightYellow = Color(0XFFFFF1BF)
        val Brown900 = Color(0xFF57443E)

        val RandomColors = listOf(
            Color.Red,
            Color.Blue,
            Color.Green,
            Color.Yellow,
            Color.Magenta,
            Color.Cyan
        )

        val NormalColor: Color
            @Composable
            get() = if (darkTheme) Color.White else Color.Black

        val NormalColorInverted: Color
            @Composable
            get() = if (darkTheme) Color.Black else Color.White

        val ChatDetailBottomSheetColor: Color
            @Composable
            get() = if (darkTheme) Color.Gray else Beige50

        val chatListTextColor: Color
            @Composable
            get() = if (darkTheme) Gray100 else Gray250

        val SettingDividerColor: Color
            @Composable
            get() = if (darkTheme) Gray300 else Beige100

        val NormalButtonColor: Color
            @Composable
            get() = if (darkTheme) CharcoalGray else Yellow700

        val PositiveButtonColor: Color
            @Composable
            get() = if (darkTheme) CharcoalGray else Yellow700

        val NegativeButtonColor: Color
            @Composable
            get() = if (darkTheme) CharcoalGray else Pink500

        val BottomNavigationSelectedColor: Color
            @Composable
            get() = if (darkTheme) Gray200 else Color.Black

        val BottomNavigationUnSelectedColor: Color
            @Composable
            get() = if (darkTheme) Color.Black else Gray200

        val CardColor: Color
            @Composable
            get() = if (darkTheme) DeepOceanBlue else Beige150

        val SettingScreenColor: Color
            @Composable
            get() = if (darkTheme) Color.Gray else Brown900

        val DialogColor: Color
            @Composable
            get() = if (darkTheme) Gray250 else Beige50
    }
}
