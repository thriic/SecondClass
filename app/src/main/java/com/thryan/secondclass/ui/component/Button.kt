package com.thryan.secondclass.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

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
