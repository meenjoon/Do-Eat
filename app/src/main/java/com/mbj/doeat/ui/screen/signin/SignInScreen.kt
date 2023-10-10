package com.mbj.doeat.ui.screen.signin

import android.content.Context
import android.util.Log
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
import androidx.compose.material.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mbj.doeat.R
import com.mbj.doeat.data.remote.model.LoginRequest
import com.mbj.doeat.ui.theme.Yellow700
import com.mbj.doeat.ui.screen.signin.viewmodel.SignInViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    var isAutoLogin by remember { mutableStateOf(viewModel.isAutoLoginEnabled()) }
    val context = LocalContext.current

    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        when {
            error != null -> {
                Log.e("Kakao", "카카오 계정 로그인 실패", error)
            }
            token != null -> {
                loginWithKakaoNickName(viewModel, navHostController)
                Log.d("Kakao", "token : ${token.accessToken}")
            }
        }
    }
    viewModel.checkAccessTokenAndNavigate(navHostController)

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
                .height(55.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Yellow700)
                .clickable {
                    loginKakao(context, kakaoCallback)
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
                    style = MaterialTheme.typography.body1,
                    color = Color.Black,
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
                    viewModel.setAutoLoginEnabled(isChecked)
                    isAutoLogin = isChecked
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Yellow700,
                    checkmarkColor = Color.Black
                )
            )

            Text(text = "자동 로그인")
        }
    }
}

private fun loginWithKakaoNickName(viewModel: SignInViewModel, navHostController: NavHostController) {
    UserApiClient.instance.me { user, error ->
        when {
            error != null -> {
                Log.e("Kakao", "사용자 정보 실패", error)
            }
            user != null -> {
                if (user.id != null && user.kakaoAccount?.profile?.nickname != null && user.kakaoAccount!!.profile?.thumbnailImageUrl != null) {
                    val loginRequest = LoginRequest(user.id!!, user.kakaoAccount?.profile?.nickname!!, user.kakaoAccount?.profile?.thumbnailImageUrl!!)
                    viewModel.signIn(loginRequest, navHostController)
                }
            }
        }
    }
}

private fun loginKakao(context: Context, kakaoCallback: (OAuthToken?, Throwable?) -> Unit) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        // 카카오 설치
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("Kakao", "카카오톡 로그인 실패", error)
            }

            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                return@loginWithKakaoTalk
            }

            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
        }
    } else {
        // 카카오 미설치
        UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
    }
}
