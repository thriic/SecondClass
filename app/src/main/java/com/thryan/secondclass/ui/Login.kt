package com.thryan.secondclass.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.thryan.secondclass.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.map

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Login(modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    val loginUiState = viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        var account by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(viewModel.sharedPref.getString("account", "")!!))
        }
        var err by rememberSaveable {
            mutableStateOf(false)
        }
        var pwd by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(viewModel.sharedPref.getString("password", "")!!))
        }
        var scAccount by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        Text("第二课堂", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = account,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                account = it
                err = !account.text.all { char -> char.isDigit() }
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
            value = pwd,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { pwd = it },
            label = { Text("密码") },
            placeholder = { Text("webvpn/教务处密码")},
            isError = false,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { }
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = scAccount,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onValueChange = { scAccount = it },
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
            Button(
                modifier = modifier
                    .padding(start = 8.dp),
                onClick = { viewModel.login(account.text, pwd.text,scAccount.text) }
            ) {
                Text("Login")
            }
        }
    }
    if (loginUiState.value.fail) Dialog(message = loginUiState.value.message, viewModel = viewModel)

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
        title = { Text("Fail") },
        text = { Text(message) },
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateUiState(false)
                }
            ) {
                Text(text = "确定")
            }
        }
    )
}