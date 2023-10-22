package com.mbj.doeat.ui.component.toast

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700
import kotlinx.coroutines.launch

@Composable
fun ToastMessage(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Yellow700,
    textColor: Color = Color.Black,
    showToast: Boolean,
    showMessage: Boolean,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(showToast) {
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
                backgroundColor = backgroundColor,
                contentColor = Color.Black
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = textColor
                )
            }
        }
    )
}
