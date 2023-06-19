package com.thryan.secondclass.ui.page

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.thryan.secondclass.R
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.utils.textFromStatus

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Page(viewModel: PageViewModel) {
    viewModel.send(PageIntent.UpdateActivity)
    PageBox(viewModel)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageBox(viewModel: PageViewModel) {
    val pageState by viewModel.pageState.collectAsState()
    val listState: LazyListState = rememberLazyListState()
    val keyword = rememberSaveable { mutableStateOf("") }

    if (pageState.showingDialog) Dialog(pageState = pageState, viewModel = viewModel)

    Box(
        Modifier
            .semantics {
                isContainer = true
            }
            .zIndex(1f)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter),
            viewModel = viewModel,
            keyword = keyword
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: PageViewModel,
    keyword: MutableState<String>
) {
    var text by keyword
    var active by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    DockedSearchBar(
        modifier = modifier,
        query = text,
        tonalElevation = SearchBarDefaults.Elevation.plus(16.dp),
        colors = SearchBarDefaults.colors(dividerColor = MaterialTheme.colorScheme.inversePrimary),
        onQueryChange = {
            if (it.isEmpty()) viewModel.send(PageIntent.Search(text))
            text = it
        },
        onSearch = {
            viewModel.send(PageIntent.Search(text))
            active = false
        },
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

@ExperimentalAnimationApi
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
        val activities = if (pageState.keyword.isNotEmpty()) pageState.activities.filter {
            it.activityName.contains(pageState.keyword) ||
                    it.activityAddress.contains(pageState.keyword) ||
                    it.activityDec.contains(pageState.keyword) ||
                    it.activityHost.contains(pageState.keyword)
        } else pageState.activities
        itemsIndexed(items = activities) { index, item ->
            //判定是否符合搜索关键词
            val status = textFromStatus(item.activityStatus)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
    //val activity = (LocalContext.current as Activity)
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
