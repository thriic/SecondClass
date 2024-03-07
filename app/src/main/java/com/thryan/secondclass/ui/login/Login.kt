package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Login(modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val url = ""
    webView(
        modifier = Modifier.fillMaxSize().statusBarsPadding(), url
    )
    if (uiState.showDialog) Dialog(message = uiState.message, viewModel = viewModel)
}

@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    message: String,
    viewModel: LoginViewModel
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = { Text("登录失败") },
        text = { Text(message) },
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.send(LoginIntent.CloseDialog)
                }
            ) {
                Text(text = "确定")
            }
        }
    )
}

@Composable
fun DebouncedButton(
    modifier: Modifier = Modifier,
    outline: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    var clicked by remember {
        mutableStateOf(!enabled)
    }
    LaunchedEffect(clicked) {
        if (clicked) {
            delay(1000L)
            clicked = !clicked
        }
    }
    if (!outline)
        Button(
            modifier = modifier,
            onClick = {
                if (enabled && !clicked) {
                    clicked = true
                    onClick()
                }
            },
            content = content
        )
    else
        OutlinedButton(
            modifier = modifier,
            onClick = {
                if (enabled && !clicked) {
                    clicked = true
                    onClick()
                }
            },
            content = content
        )
}