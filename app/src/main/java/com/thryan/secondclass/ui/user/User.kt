package com.thryan.secondclass.ui.user

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thryan.secondclass.R
import com.thryan.secondclass.ui.page.Progress


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun User(navController: NavController, userViewModel: UserViewModel) {
    val userState by userViewModel.userState.collectAsState()
    //val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Info",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
            )
        },
        content = { innerPadding ->
            if (userState.loading) {
                val text = if (userState.user.name.isEmpty()) "加载用户信息"
                else "获取分数"
                Progress(text)
            } else {
                LazyColumn(
                    contentPadding = innerPadding,
                ) {
                    item {
                        BasicInfo(userViewModel)
                        HorizontalDivider(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                    }
                    item {
                        ScoreInfo(userViewModel)
                        HorizontalDivider(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
                        )
                    }
                    item {
                        OtherInfo(userState.dynamic, userState.webView, userViewModel)
                    }
                }
            }
        }
    )
}

@Composable
fun BasicInfo(userViewModel: UserViewModel) {
    val userState by userViewModel.userState.collectAsState()
    ItemSubTitle("基础")
    ListItem(
        modifier = Modifier.clickable { },
        title = "姓名",
        description = userState.user.name
    )
    ListItem(
        modifier = Modifier.clickable { },
        title = "诚信值",
        description = userState.scoreInfo.integrity_value.toString()
    )
}

@Composable
fun ScoreInfo(userViewModel: UserViewModel) {
    val userState by userViewModel.userState.collectAsState()
    ItemSubTitle("分数")
    ListItem(
        modifier = Modifier.clickable { },
        title = "已完成活动",
        description = userState.scoreInfo.activity.toString()
    )
    var expand by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable { expand = !expand },
        title = "总分",
        description = userState.scoreInfo.score.toString()
    ) {
        IconButton(modifier = it, onClick = { expand = !expand }) {
            Icon(
                painter = if (expand) painterResource(R.drawable.expand_less) else painterResource(R.drawable.expand_more),
                contentDescription = if (expand) "展开" else "收起"
            )
        }
    }
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = expand,
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val configuration = LocalConfiguration.current
            ComposeRadarView(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        (minOf(
                            configuration.screenWidthDp,
                            configuration.screenHeightDp
                        ) / 3 * 2).dp
                    ),
                userState.radarScore,
                true
            )
        }
    }

}

@Composable
fun OtherInfo(dynamicChecked: Boolean, webViewChecked: Boolean, viewModel: UserViewModel) {
    ItemSubTitle("设置")

    val context = LocalContext.current
    ListItem(
        modifier = Modifier.clickable { },
        description = "通过WebView登录",
    ) { modifier ->
        Switch(
            modifier = modifier,
            thumbContent = { SwitchIcon(checked = webViewChecked) },
            checked = webViewChecked,
            onCheckedChange = {
                viewModel.send(UserIntent.ChangeWebView(it))
                Toast.makeText(context, "下一次打开app生效", Toast.LENGTH_SHORT).show()
            })
    }
    ListItem(
        modifier = Modifier.clickable { },
        description = "动态配色",
    ) { modifier ->
        Switch(
            modifier = modifier,
            thumbContent = { SwitchIcon(checked = dynamicChecked) },
            checked = dynamicChecked,
            onCheckedChange = {
                viewModel.send(UserIntent.ChangeDynamic(it))
                Toast.makeText(context, "下一次打开app生效", Toast.LENGTH_SHORT).show()
            })
    }
    val showDialog = remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable { showDialog.value = true },
        description = "注意事项"
    ) {
        IconButton(modifier = it, onClick = { showDialog.value = true }) {
            Icon(
                Icons.Default.Info,
                contentDescription = "注意事项"
            )
        }
    }
    if (showDialog.value) {
        Dialog(text = stringResource(id = R.string.about), showDialog = showDialog)
    }
}

@Composable
fun ListItem(
    modifier: Modifier,
    title: String = "",
    description: String = "",
    content: @Composable (Modifier) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.align(Alignment.CenterStart)) {
            if (title.isNotEmpty()) Text(text = title, fontSize = 12.sp)
            if (description.isNotEmpty()) Text(text = description, fontSize = 15.sp)
        }
        content(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun ItemSubTitle(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = title, color = MaterialTheme.colorScheme.surfaceTint, fontSize = 13.sp)
    }
}

@Composable
fun SwitchIcon(checked: Boolean) {
    if (checked) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Check",
            modifier = Modifier.size(SwitchDefaults.IconSize),
        )
    }
}


@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    text: String,
    showDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = { Text("注意事项") },
        text = { Text(text) },
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = { showDialog.value = false }
            ) {
                Text(text = "OK")
            }
        }
    )
}
