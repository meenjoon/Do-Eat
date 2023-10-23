package com.mbj.doeat.ui.component.party

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoPartiesAvailable(modifier: Modifier = Modifier.size(120.dp)) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "No parties available",
        modifier = modifier,
        tint = Color.Red
    )
    Text(
        text = "현재 개설된 파티가 없습니다.",
        fontSize = 20.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}
