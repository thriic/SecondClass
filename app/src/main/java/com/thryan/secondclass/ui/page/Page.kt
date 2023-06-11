package com.thryan.secondclass.ui.page

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thryan.secondclass.core.result.SCActivity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.thryan.secondclass.ui.login.HttpStatus

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Page(viewModel: PageViewModel) {
    val listState: LazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.snackbarHostState = snackbarHostState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        PageBox(viewModel, listState = listState)
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PageBox(viewModel: PageViewModel, listState: LazyListState) {

    val pageUiState by viewModel.uiState.collectAsState()
    val httpState by viewModel.httpState.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val openDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    if (openDialog.value) Dialog(message = dialogMessage.value, openDialog = openDialog)

    Box(
        Modifier
            .semantics {
                isContainer = true
            }
            .zIndex(1f)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)) {
        DockedSearchBar(
            modifier = Modifier.align(Alignment.TopCenter),
            query = text,
            tonalElevation = SearchBarDefaults.Elevation.plus(16.dp),
            colors = SearchBarDefaults.colors(dividerColor = MaterialTheme.colorScheme.inversePrimary),
            onQueryChange = { text = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = { Text("Search") },
            leadingIcon = {
                if (active)
                    IconButton(onClick = { active = false }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                else
                    IconButton(onClick = { active = true }) {
                        Icon(Icons.Default.Search, null)
                    }
            },
            trailingIcon = {
                IconButton(onClick = {
                    expanded = true
                    active = false
                }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("用户信息") },
                            onClick = {
                                openDialog.value = true
                                val si = viewModel.getScoreInfo()
                                val user = viewModel.getUserInfo()
                                dialogMessage.value = buildString {
                                    append(user.name)
                                    append("\n积分:")
                                    append(si.score)
                                    append(" 完成活动:")
                                    append(si.activity)
                                    append(" 诚信值:")
                                    append(si.integrity_value)
                                }
                                expanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("使用须知") },
                            onClick = {
                                openDialog.value = true
                                dialogMessage.value = "此软件提供在任意时间对已报名活动进行签到的功能，且后台记录数据为活动进行中的时间。\n理论上不存在风险，但需要注意二课的审核机制，鉴别该活动是否与自己相关再进行报名签到\n本项目仅供开发学习使用"
                                expanded = false
                            })
                    }
                }
            },
        ) {

        }

    }
    Box(modifier = Modifier.fillMaxWidth()) {
        if (httpState.httpStatus == HttpStatus.Pending)
            Progress(httpState)
        else {
            ActivityList(activities = pageUiState, viewModel = viewModel, listState = listState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Progress(httpState: HttpState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(httpState.message, modifier = Modifier.padding(top = 16.dp))
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityList(
    activities: List<SCActivity>,
    viewModel: PageViewModel,
    listState: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        //contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    ) {
        item {
            Spacer(
                Modifier.size(
                    WindowInsets.systemBars.asPaddingValues().calculateTopPadding().plus(16.dp)
                        .plus(SearchBarDefaults.InputFieldHeight)
                )
            )
        }
        Log.i("Page", "${activities.size}")
        itemsIndexed(items = activities) { index, item ->
            val status = when (item.activityStatus) {
                "0" -> if (item.isSign == "1") "已报名" else "报名中"
                "1" -> "待开始"
                "2" -> "进行中"
                "3" -> "待完结"
                "5" -> "已完结"
                else -> "未知"
            }
            var expand by remember { mutableStateOf(false) }
            AnimatedContent(
                targetState = expand,
                transitionSpec = {
                    fadeIn(animationSpec = tween(10, 100)) with
                            fadeOut(animationSpec = tween(0)) using
                            SizeTransform { initialSize, targetSize ->
                                if (targetState) {
                                    keyframes {
                                        // Expand horizontally first.
                                        IntSize(targetSize.width, initialSize.height) at 150
                                        durationMillis = 300
                                    }
                                } else {
                                    keyframes {
                                        // Shrink vertically first.
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                    }
                                }
                            }
                }
            ) { targetExpanded ->

                if (targetExpanded) {
                    ActivityRowExpand(
                        activity = item,
                        viewModel = viewModel,
                        status = status
                    ) { expand = !expand }
                } else {
                    ActivityRow(activity = item, viewModel = viewModel, status = status) {
                        expand = !expand
                    }
                }
            }
        }

    }
}

//Container transform
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityRow(
    activity: SCActivity,
    viewModel: PageViewModel,
    status: String,
    onClick: () -> Unit
) {
    val padding = 16.dp
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(activity.activityName, style = MaterialTheme.typography.titleLarge)
            Text(
                activity.activityDec.replace("\\n", ""),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 7
            )
            Text(
                activity.startTime.slice(5..15) + " 至 " + activity.endTime.slice(5..15),
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            status
                        )
                    },
                    colors = if (status == "报名中") SuggestionChipDefaults.suggestionChipColors(
                        disabledLabelColor = MaterialTheme.colorScheme.primary
                    ) else SuggestionChipDefaults.suggestionChipColors(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityRowExpand(
    activity: SCActivity,
    viewModel: PageViewModel,
    status: String,
    onClick: () -> Unit
) {
    val padding = 16.dp
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding)
            .clip(CardDefaults.shape),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(activity.activityName, style = MaterialTheme.typography.titleLarge)
            Text(
                activity.activityDec.replace("\\n", ""),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                activity.startTime.slice(5..15) + " 至 " + activity.endTime.slice(5..15),
                style = MaterialTheme.typography.labelMedium
            )
            Text("学院: ${activity.activityHost}", style = MaterialTheme.typography.labelMedium)
            Text("报名人数: ${activity.signNum}", style = MaterialTheme.typography.labelMedium)
            if (status != "报名中")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                status
                            )
                        },
                    )
                    if (activity.isSign == "1" && status != "已报名")
                        SuggestionChip(
                            onClick = {},
                            enabled = false,
                            label = {
                                Text(
                                    "已报名"
                                )
                            }
                        )
                }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (status == "报名中")
                    OutlinedButton(onClick = { viewModel.sign(activity) }) {
                        Text("报名")
                    }
                if (activity.isSign == "1")
                    OutlinedButton(onClick = { viewModel.signIn(activity) }) {
                        Text("签到")
                    }
            }

        }
    }
}

@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    message: String,
    openDialog: MutableState<Boolean>
) {
    //val activity = (LocalContext.current as Activity)
    AlertDialog(
        onDismissRequest = {
        },
        title = { Text("第二课堂") },
        text = { Text(message) },
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text(text = "OK")
            }
        }
    )
}
