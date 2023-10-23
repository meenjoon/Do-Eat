package com.mbj.doeat.ui.component.searchbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mbj.doeat.ui.model.SearchWidgetState
import com.mbj.doeat.ui.theme.Color.Companion.Beige100
import com.mbj.doeat.ui.theme.Color.Companion.Yellow700

@Composable
fun MainAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    backgroundColor: Color,
    contentColor: Color,
    defaultAppBarText: String,
    searchAppBarText: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                defaultAppBarText = defaultAppBarText,
                onSearchClicked = onSearchTriggered
            )
        }

        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                searchAppBarText = searchAppBarText,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@Composable
fun DefaultAppBar(
    backgroundColor: Color,
    contentColor: Color,
    defaultAppBarText: String,
    onSearchClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = defaultAppBarText,
                color = contentColor
            )
        },
        backgroundColor = backgroundColor,
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = contentColor
                )
            }
        }
    )
}

@Composable
fun SearchAppBar(
    text: String,
    height: Dp ? = null,
    leftAndRightPaddingDp: Dp ? = null,
    backgroundColor: Color,
    contentColor: Color,
    searchAppBarText: String,
    roundedCornerShape: RoundedCornerShape? = null,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height ?: 56.dp)
            .padding(leftAndRightPaddingDp ?: 0.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = backgroundColor,
        shape = roundedCornerShape ?: RoundedCornerShape(0.dp)
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = searchAppBarText,
                    color = contentColor
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                color = contentColor
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = contentColor
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = contentColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                cursorColor = contentColor.copy(alpha = ContentAlpha.medium)
            ))
    }
}


@Composable
@Preview
fun DefaultAppBarPreview() {
    DefaultAppBar(
        backgroundColor = Yellow700,
        contentColor = Color.Black,
        defaultAppBarText = "앱바 이름",
        onSearchClicked = {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(
        text = "",
        backgroundColor = Beige100,
        contentColor = Color.Black,
        searchAppBarText = "힌트 메세지",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}
