package com.mbj.doeat.ui.component.webview

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.mbj.doeat.BuildConfig
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700
import com.mbj.doeat.util.UrlUtils

@Composable
fun ReusableWebView(
    url: String?,
    restaurantName: String,
    webViewModifier: Modifier,
    onWebViewCreated: (WebView) -> Unit
) {
    val webViewClient = AccompanistWebViewClient()
    val webChromeClient = AccompanistWebChromeClient()
    val webViewState = initializeWebView(url, restaurantName)
    val webViewNavigator = rememberWebViewNavigator()

    Box {
        WebView(
            state = webViewState,
            navigator = webViewNavigator,
            client = webViewClient,
            chromeClient = webChromeClient,
            onCreated = { webView ->
                onWebViewCreated(webView)
                with(webView) {
                    settings.run {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        javaScriptCanOpenWindowsAutomatically = false
                    }
                }
            },
            modifier = webViewModifier
        )

        IconButton(
            onClick = {
                if (webViewNavigator.canGoBack) {
                    webViewNavigator.navigateBack()
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로 가기",
                tint = Yellow700
            )
        }
    }
}

@Composable
fun initializeWebView(url: String?, restaurantName: String) = rememberWebViewState(
    url = getUrl(url = url, restaurantName = restaurantName),
    additionalHttpHeaders = emptyMap()
)

fun getUrl(url: String?, restaurantName: String): String {
    return if (url == "") {
        "${BuildConfig.NAVER_SEARCH_BASE_URL}search.naver?query=${restaurantName}"
    } else {
        UrlUtils.decodeUrl(url ?: "")
    }
}
