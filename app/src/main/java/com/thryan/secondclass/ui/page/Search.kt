package com.thryan.secondclass.ui.page

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.thriic.common.data.ActivityStatus
import cn.thriic.common.data.ActivityType
import com.thryan.secondclass.R

val statusList = ActivityStatus.activityStatusMap.values.toList()
val typeList = ActivityType.activityTypeMap.values.toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    modifier: Modifier = Modifier,
    viewModel: PageViewModel,
    trailingIcon: @Composable (() -> Unit)
) {
    val filterState by viewModel.filterState.collectAsState()

    var keyword by rememberSaveable { mutableStateOf("") }
    var onlySign by rememberSaveable { mutableStateOf(filterState.onlySign) }
    var status by rememberSaveable { mutableStateOf(filterState.status) }
    var type by rememberSaveable { mutableStateOf(filterState.type) }

    var active by rememberSaveable { mutableStateOf(false) }
    val padding: Int by animateIntAsState(if (active) 0 else 16)
    SearchBar(
        modifier = modifier.padding(horizontal = padding.dp),
        query = keyword,
        onQueryChange = { keyword = it },
        onSearch = {
            viewModel.send(PageIntent.Search(keyword, onlySign, status, type))
            active = false
        },
        active = active,
        onActiveChange = {
            if (!it) viewModel.send(PageIntent.Search(keyword, onlySign, status, type))
            active = it
        },
        placeholder = { Text("搜索活动") },
        leadingIcon = {
            if (active) IconButton(onClick = {
                active = false
                viewModel.send(PageIntent.Search(keyword, onlySign, status, type))
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            } else Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (active) {
                IconButton(onClick = { keyword = "" }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            } else {
                trailingIcon()
            }
        },
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Filter(label = "仅报名", select = onlySign) { selected, _ ->
                    onlySign = selected
                }
            }

            item {
                Filter(
                    label = if (status == "") "状态" else status,
                    select = status != "",
                    list = statusList
                ) { selected, choose ->
                    status = if (selected)
                        choose
                    else
                        ""
                }
            }

            item {
                Filter(
                    label = if (type == "") "类型" else type,
                    select = type != "",
                    list = typeList
                ) { selected, choose ->
                    type = if (selected)
                        choose
                    else
                        ""
                }
            }
        }

        LazyColumn {
            item {
                ListItem(
                    headlineContent = { Text("清空搜索框及过滤器") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    modifier = Modifier
                        .clickable {
                            keyword = ""
                            onlySign = false
                            status = ""
                            type = ""
                            active = false
                            viewModel.send(PageIntent.Search())
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
                    supportingContent = { Text("快捷过滤: 仅报名 进行中") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    modifier = Modifier
                        .clickable {
                            onlySign = true
                            status = "进行中"
                            type = ""
                            active = false
                            viewModel.send(PageIntent.Search(onlySign = true, status = "进行中"))
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filter(
    label: String,
    select: Boolean = false,
    list: List<String>? = null,
    onSelect: (selected: Boolean, choose: String) -> Unit
) {
    var selected by remember { mutableStateOf(select) }
    var expanded by remember { mutableStateOf(false) }
    var choose by remember { mutableStateOf(label) }
    FilterChip(
        selected = true,
        label = { Text(choose) },
        onClick = {
            if (list != null) {
                if (choose in list) {
                    selected = !selected
                    onSelect(selected, choose)
                } else {
                    expanded = !expanded
                }
            } else {
                selected = !selected
                onSelect(selected, choose)
            }
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        trailingIcon = {
            if (list != null) {
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                ) {
                    Icon(
                        painterResource(if (expanded) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down),
                        contentDescription = "drop"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    repeat(list.size) {
                        DropdownMenuItem(
                            text = { Text(list[it]) },
                            onClick = {
                                expanded = false
                                choose = list[it]
                                selected = true
                                onSelect(selected, choose)
                            })
                    }
                }
            }
        })
}