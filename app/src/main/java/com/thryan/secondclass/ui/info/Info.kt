package com.thryan.secondclass.ui.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.thryan.secondclass.R
import com.thryan.secondclass.core.result.signIn
import com.thryan.secondclass.core.utils.formatDate
import com.thryan.secondclass.core.utils.formatTime
import com.thryan.secondclass.core.utils.toLocalDateTime
import com.thryan.secondclass.ui.info.Dialog.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Info(navController: NavController, viewModel: InfoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { viewModel.snackbarState }
    val listState: LazyListState = rememberLazyListState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { InfoAppBar(scrollBehavior, uiState) { navController.popBackStack() } },
        content = { innerPadding ->
            if (uiState.loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                InfoColumn(
                    innerPadding = innerPadding,
                    uiState = uiState,
                    viewModel = viewModel,
                    listState = listState
                )
            }
        }
    )
    when (uiState.showDialog) {
        SignInTime -> TimePicker(
            title = "选择签到时间",
            initialTime = uiState.signInTime.toLocalTime(),
            onClose = { viewModel.send(InfoIntent.CloseDialog) }
        ) {
            viewModel.send(InfoIntent.UpdateSignInTime(it))
        }

        SignInDate -> DatePicker(
            title = "选择签到日期",
            initialDate = uiState.signInTime.toLocalDate(),
            onClose = { viewModel.send(InfoIntent.CloseDialog) }
        ) {
            viewModel.send(InfoIntent.UpdateSignInDate(it))
        }

        SignOutTime -> TimePicker(
            title = "选择签退时间",
            initialTime = uiState.signOutTime.toLocalTime(),
            onClose = { viewModel.send(InfoIntent.CloseDialog) }
        ) {
            viewModel.send(InfoIntent.UpdateSignOutTime(it))
        }

        SignOutDate -> DatePicker(
            title = "选择签退日期",
            initialDate = uiState.signOutTime.toLocalDate(),
            onClose = { viewModel.send(InfoIntent.CloseDialog) }
        ) {
            viewModel.send(InfoIntent.UpdateSignOutDate(it))
        }

        null -> LocalFocusManager.current.clearFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoAppBar(scrollBehavior: TopAppBarScrollBehavior, uiState: InfoState, onClick: () -> Unit) {
    LargeTopAppBar(
        title = {
            Text(
                uiState.activity.activityName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        actions = {},
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun InfoColumn(
    innerPadding: PaddingValues,
    uiState: InfoState,
    viewModel: InfoViewModel,
    listState: LazyListState
) {
    LazyColumn(
        contentPadding = innerPadding,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(
                Modifier
                    .height(16.dp)
                    .fillMaxWidth()
            )
            MainCard(uiState = uiState)
        }

        item {
            SignCard(uiState = uiState, viewModel = viewModel)
        }
        if (uiState.activity.isSign == "1") item {
            SignInCard(uiState = uiState, viewModel = viewModel)
        }
        item {
            LinkCard(uiState = uiState, viewModel = viewModel)
        }

        item {
            Spacer(
                Modifier
                    .height(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoCard(content: @Composable (ColumnScope.() -> Unit)) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

@Composable
fun MainCard(uiState: InfoState) {
    InfoCard {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SuggestionChip(
                onClick = {},
                enabled = false,
                label = {
                    Text("${uiState.activity.activityIntegral}")
                },
                colors = SuggestionChipDefaults.suggestionChipColors(disabledLabelColor = MaterialTheme.colorScheme.primary)
            )

            SuggestionChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(uiState.activity.status)
                },
                colors = SuggestionChipDefaults.suggestionChipColors(disabledLabelColor = MaterialTheme.colorScheme.primary)
            )
        }
        Text(uiState.activity.activityDec, style = MaterialTheme.typography.bodyLarge)
        Text(
            "主办方: " + uiState.activity.activityHost,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            "地点: " + uiState.activity.activityAddress,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            "类型: " + uiState.activity.type,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            uiState.activity.startTime.slice(5..15) + " 至 " + uiState.activity.endTime.slice(5..15),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SignCard(uiState: InfoState, viewModel: InfoViewModel) {
    InfoCard {
        Text(
            if (uiState.activity.isSign == "1") "已报名" else "未报名",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "报名人数: " + uiState.activity.signNum + "/" + if (uiState.activity.activityNum == Int.MAX_VALUE) "无限制" else uiState.activity.activityNum,
            style = MaterialTheme.typography.bodyMedium
        )
        Text("报名截止: " + uiState.activity.signTime, style = MaterialTheme.typography.bodyMedium)
        if (uiState.activity.activityStatus in listOf("1", "2") && uiState.activity.isSign == "0")
            Text("报名待开始或进行中的活动存在风险", style = MaterialTheme.typography.labelMedium)
        if (uiState.activity.isSign == "0" &&
            uiState.activity.activityStatus in listOf("0", "1", "2")
        ) Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = { viewModel.send(InfoIntent.Sign) }) {
                Text("报名")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInCard(uiState: InfoState, viewModel: InfoViewModel) {
    val keyboard = LocalSoftwareKeyboardController.current
    InfoCard {
        val (_, signInTime, signOutTime) = uiState.signInfo
        Text(
            when {
                signInTime.isNotEmpty() && signOutTime.isNotEmpty() -> "已签到签退"
                signInTime.isNotEmpty() && signOutTime.isEmpty() -> "已签到未签退"
                signInTime.isEmpty() && signOutTime.isEmpty() -> "未签到签退"
                else -> "签到"
            }, style = MaterialTheme.typography.titleMedium
        )
        if (uiState.activity.activityStatus in listOf("0", "1"))
            Text("签到报名中或待开始的活动存在风险", style = MaterialTheme.typography.labelMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.signInTime.formatDate(),
                onValueChange = {},
                enabled = uiState.signInfo.signInTime.isEmpty(),
                label = { Text("${if (uiState.signInfo.signInTime.isEmpty()) "选择" else ""}签到日期") },
                modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        viewModel.send(InfoIntent.ShowDialog(SignInDate))
                        keyboard?.hide()
                    }
                }.weight(1f)
            )
            OutlinedTextField(
                value = uiState.signInTime.formatTime(),
                onValueChange = {},
                enabled = uiState.signInfo.signInTime.isEmpty(),
                label = { Text("签到时间") },
                modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        viewModel.send(InfoIntent.ShowDialog(SignInTime))
                        keyboard?.hide()
                    }
                }.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.signOutTime.formatDate(),
                onValueChange = {},
                enabled = uiState.signInfo.signOutTime.isEmpty(),
                label = { Text("${if (uiState.signInfo.signOutTime.isEmpty()) "选择" else ""}签退日期") },
                modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        viewModel.send(InfoIntent.ShowDialog(SignOutDate))
                        keyboard?.hide()
                    }
                }.weight(1f)
            )
            OutlinedTextField(
                value = uiState.signOutTime.formatTime(),
                onValueChange = {},
                enabled = uiState.signInfo.signOutTime.isEmpty(),
                label = { Text("签退时间") },
                modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        viewModel.send(InfoIntent.ShowDialog(SignOutTime))
                        keyboard?.hide()
                    }
                }.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (!uiState.signInfo.signIn()) OutlinedButton(
                modifier = Modifier.padding(start = 16.dp),
                onClick = { viewModel.send(InfoIntent.SignIn) }) {
                Text("签到签退")
            }
        }
    }
}

@Composable
fun LinkCard(uiState: InfoState, viewModel: InfoViewModel) {
    InfoCard {
        Text("签到签退链接(长按文本复制)", style = MaterialTheme.typography.titleMedium)
        SelectionContainer {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column {
                    DisableSelection {
                        Text("webvpn:", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        stringResource(R.string.vpn_link) + uiState.link,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column {
                    DisableSelection {
                        Text("校园网:", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        stringResource(R.string.link) + uiState.link,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = { viewModel.send(InfoIntent.GenerateLink) }) {
                Text("重新生成")
            }
        }
    }
}
