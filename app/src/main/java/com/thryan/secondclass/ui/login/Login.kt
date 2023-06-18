package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.thryan.secondclass.R
import kotlinx.coroutines.delay


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Login(modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LoginContent(uiState = uiState, viewModel = viewModel)
    }
    if (uiState.showDialog) Dialog(message = uiState.message, viewModel = viewModel)
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
        placeholder = { Text("webvpn/教务处密码") },
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
                    else painterResource(
                        R.drawable.visibility_off
                    ),
                    "",
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation()
    )

    OutlinedTextField(
        value = uiState.scAccount,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        onValueChange = { viewModel.send(LoginIntent.UpdateSCAccount(it)) },
        label = { Text("二课账号(可不填)") },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { }
        )
    )
    Text("不填则使用登录webvpn的学号和默认密码", style = MaterialTheme.typography.labelSmall)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        DebouncedButton(
            modifier = Modifier.padding(start = 8.dp),
            outline = false,
            onClick = {
                viewModel.send(LoginIntent.Login)
            }
        ) {
            Text("Login")
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
    outline: Boolean = true,
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