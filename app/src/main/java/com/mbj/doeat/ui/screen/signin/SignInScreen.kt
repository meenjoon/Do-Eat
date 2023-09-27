package com.mbj.doeat.ui.screen.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mbj.doeat.R
import com.mbj.doeat.ui.theme.Yellow700

@Composable
fun SignInScreen() {
    var isAutoLogin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로그인 버튼
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Yellow700)
                .clickable {
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.signin_kakao),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "카카오 로그인",
                    style = MaterialTheme.typography.body1
                )
            }
        }

        // 자동 로그인 체크 박스
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = isAutoLogin,
                onCheckedChange = { isChecked ->
                    isAutoLogin = isChecked
                },
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(text = "자동 로그인")
        }
    }
}
