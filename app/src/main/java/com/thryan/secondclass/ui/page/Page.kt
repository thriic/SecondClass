package com.thryan.secondclass.ui.page

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.thryan.secondclass.R
import com.thryan.secondclass.core.result.SCActivity

@Composable
fun Page(viewModel: PageViewModel) {
    viewModel.send(PageIntent.UpdateActivity)
    PageBox(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageBox(viewModel: PageViewModel) {
    val pageState by viewModel.pageState.collectAsState()
    val listState: LazyListState = rememberLazyListState()

    if (pageState.showingDialog) Dialog(pageState = pageState, viewModel = viewModel)

    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f },
            query = text,
            onQueryChange = { text = it },
            onSearch = {
                viewModel.send(PageIntent.Search(text))
                active = false
            },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = { Text("搜索活动") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        MenuItem("用户信息") {
                            viewModel.send(PageIntent.ShowDialog(userInfo = true))
                            expanded = false
                        }
                        val aboutString = stringResource(R.string.about)
                        MenuItem("使用须知") {
                            viewModel.send(PageIntent.ShowDialog(aboutString))
                            expanded = false
                        }
                    }
                }
            },
        ) {
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text("清空搜索框") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        modifier = Modifier
                            .clickable {
                                text = ""
                                active = false
                                viewModel.send(PageIntent.Search(text))
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("tips 1") },
                        supportingContent = { Text("回车键搜索") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        modifier = Modifier
                            .clickable {}
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("tips 2") },
                        supportingContent = { Text("是的，因为懒得做搜索历史所以用这些水布局") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        modifier = Modifier
                            .clickable {}
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (pageState.loading)
            Progress(pageState.loadingMsg)
        else {
            ActivityList(
                pageState = pageState,
                viewModel = viewModel,
                listState = listState
            )
        }
    }
}


@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick
    )
}

@Composable
fun Progress(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(text = text, modifier = Modifier.padding(top = 16.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityList(
    pageState: PageState,
    viewModel: PageViewModel,
    listState: LazyListState = rememberLazyListState()
) {
    val incompleteId = stringResource(R.string.incomplete_id)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
        itemsIndexed(items = pageState.activities) { index, item ->
            //判定是否符合搜索关键词
            val status = item.status
            ActivityRow(activity = item, status = status) {
                if (item.id.contains("***")) {
                    viewModel.send(PageIntent.ShowDialog(incompleteId))
                } else
                    viewModel.send(PageIntent.OpenActivity(item.id))
            }
            if (pageState.loadMore && pageState.activities.size - index < 2) {
                viewModel.send(PageIntent.LoadMore)
            }
        }
        if (pageState.loadMore) item {
            //这里应该有个全部加载完成后显示”加载完毕“
            //活动数量太多了，我相信没人无聊到拉完
            //Lazy
            Progress(text = "")
        }

    }
}

//Container transform
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityRow(
    activity: SCActivity,
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
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(activity.activityName, style = MaterialTheme.typography.titleMedium)
            Text(
                activity.activityDec.replace("\\n", ""),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 7
            )
            Text(
                activity.startTime.slice(5..15) + " 至 " + activity.endTime.slice(5..15),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                activity.type,
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
        }
    }
}


@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    pageState: PageState,
    viewModel: PageViewModel
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = { Text("第二课堂") },
        text = { Text(pageState.dialogContent) },
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.send(PageIntent.CloseDialog)
                }
            ) {
                Text(text = "OK")
            }
        }
    )
}
