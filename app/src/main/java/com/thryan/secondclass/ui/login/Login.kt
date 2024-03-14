package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Login(modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val url = "http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/mSsoLogin"
    WebView(
        Modifier.fillMaxSize().statusBarsPadding(), viewModel, url
    )
    if (uiState.pending) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(with(MaterialTheme.colorScheme.background) {
                    Color(red, green, blue, 0.5f)
                })
                // 拦截对WebView的点击
                .clickable(onClick = {}, interactionSource = interactionSource, indication = null)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp)
            )
        }
    }
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