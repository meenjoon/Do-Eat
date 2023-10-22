package com.mbj.doeat.ui.component.button

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun BackButton(modifier: Modifier = Modifier,navController: NavHostController) {
    Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "뒤로 가기",
        modifier = modifier.clickable {
            navController.popBackStack()
        }
    )
}
