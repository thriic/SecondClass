package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.thryan.secondclass.R
import kotlinx.coroutines.delay


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Login(modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    //判断是否选择使用WebView
    //目前二课可通过默认密码且无验证码登录，默认不使用WebView
    if (uiState.webView) {
        WebView(viewModel = viewModel)
    } else {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoginContent(uiState = uiState, viewModel = viewModel)
        }
    }
    if (uiState.showDialog) Dialog(message = uiState.message, viewModel = viewModel)
}

@Composable
fun WebView(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val url = "http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/mSsoLogin"
    WebView(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(), viewModel, url
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    "加载中，请耐心等待",
                )
            }
        }
    }
}

@Composable
fun LoginContent(uiState: LoginState, viewModel: LoginViewModel) {
    var err by rememberSaveable {
        mutableStateOf(false)
    }


    Text(
        "第二课堂",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    OutlinedTextField(
        value = uiState.account,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(),
        onValueChange = {
            viewModel.send(LoginIntent.UpdateAccount(it))
            err = !it.all { char -> char.isDigit() }
        },
        label = { Text("学号") },
        isError = err,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { }
        ),
        supportingText = {
            if (err) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "仅数字",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
    OutlinedTextField(
        value = uiState.password,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = { viewModel.send(LoginIntent.UpdatePassword(it)) },
        label = { Text("密码") },
        placeholder = { Text("EasyConnect/教务处密码") },
        isError = false,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { }
        ),
        trailingIcon = {
            IconButton(onClick = { viewModel.send(LoginIntent.UpdatePasswordVisible(!uiState.showPassword)) }) {
                Icon(
                    if (uiState.showPassword) painterResource(R.drawable.visibility)
                    else painterResource(R.drawable.visibility_off),
                    "visible",
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation()
    )

    OutlinedTextField(
        value = uiState.scPassword,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        onValueChange = { viewModel.send(LoginIntent.UpdateSCAccount(it)) },
        label = { Text("二课密码") },
        placeholder = { Text("默认为123456,可不填") },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { }
        )
    )
    Row {
        Text(
            "无法登录可",
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            "通过网页登录",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                viewModel.send(LoginIntent.ChangeWebView)
            }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        DebouncedButton(
            modifier = Modifier.padding(start = 8.dp),
            enabled = !uiState.pending,
            onClick = {
                viewModel.send(LoginIntent.Login)
            }
        ) {
            Text(if (uiState.pending) "Login..." else "Login")
        }
    }
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