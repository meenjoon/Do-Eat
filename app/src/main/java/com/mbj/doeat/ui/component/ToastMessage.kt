package com.mbj.doeat.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.theme.Yellow700
import kotlinx.coroutines.launch

@Composable
fun ToastMessage(
    modifier: Modifier = Modifier,
    showMessage: Boolean,
    message: String,
    clickCount: Int,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(clickCount) {
        if (showMessage) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = duration
                )
            }
        }
    }

    SnackbarHost(
        modifier = modifier.padding(16.dp),
        hostState = snackbarHostState,
        snackbar = {
            Snackbar(
                backgroundColor = Yellow700,
                contentColor = Color.Black
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
