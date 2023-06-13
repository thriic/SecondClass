package com.thryan.secondclass.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Info(navController: NavController, viewModel: InfoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { InfoAppBar(scrollBehavior, uiState) { navController.popBackStack() } },
        content = { innerPadding ->
            InfoColumn(innerPadding = innerPadding, uiState = uiState, viewModel = viewModel)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoAppBar(scrollBehavior: TopAppBarScrollBehavior, uiState: InfoState, onClick: () -> Unit) {
    LargeTopAppBar(
        title = {
            Text(
                uiState.activity.activityName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {},
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun InfoColumn(innerPadding: PaddingValues, uiState: InfoState, viewModel: InfoViewModel) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
        item {
            SignInCard(uiState = uiState, viewModel = viewModel)
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
        elevation = CardDefaults.cardElevation(),
        //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
            modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        when (uiState.activity.activityStatus) {
                            "0" -> "报名中"
                            "1" -> "待开始"
                            "2" -> "进行中"
                            "3" -> "待完结"
                            "5" -> "已完结"
                            else -> "未知"
                        }
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(disabledLabelColor = MaterialTheme.colorScheme.primary)
            )
        }
        Text(uiState.activity.activityDec)
        Text(
            uiState.activity.startTime.slice(5..15) + " 至 " + uiState.activity.endTime.slice(5..15),
            style = MaterialTheme.typography.labelMedium
        )
        Text("主办方: " + uiState.activity.activityHost)
        Text("地点: " + uiState.activity.activityAddress)
    }
}

@Composable
fun SignCard(uiState: InfoState, viewModel: InfoViewModel) {
    InfoCard {
        Text(if (uiState.activity.isSign == "1") "已报名" else "未报名")
        Text("报名人数: " + uiState.activity.signNum + "/" + uiState.activity.activityNum)
        Text("报名截止: " + uiState.activity.signTime)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text("报名")
            }
        }
    }
}

@Composable
fun SignInCard(uiState: InfoState, viewModel: InfoViewModel) {
    InfoCard {
        val (_, signInTime, signOutTime) = uiState.signInfo
        Text(
            when {
                signInTime.isNotEmpty() && signOutTime.isNotEmpty() -> "已签到签退"
                signInTime.isNotEmpty() && signOutTime.isEmpty() -> "已签到未签退"
                signInTime.isEmpty() && signOutTime.isEmpty() -> "未签到签退"
                else -> "签到"
            }
        )
        OutlinedTextField(
            value = uiState.activity.startTime.after(10),
            onValueChange = {},
            label = { Text("签到时间") },
            modifier = Modifier.clickable(true) {

            }
        )
        OutlinedTextField(
            value = uiState.activity.endTime.before(10),
            onValueChange = {},
            label = { Text("签退时间") },
            modifier = Modifier.clickable(true) {

            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text("生成链接")
            }
            OutlinedButton(modifier = Modifier.padding(start = 16.dp), onClick = { /*TODO*/ }) {
                Text("签到签退")
            }
        }
    }
}