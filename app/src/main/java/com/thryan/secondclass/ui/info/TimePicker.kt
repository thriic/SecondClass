package com.thryan.secondclass.ui.info

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import com.thryan.secondclass.ui.info.InfoIntent
import com.thryan.secondclass.ui.info.InfoViewModel
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    title: String,
    viewModel: InfoViewModel,
    onTimeChange: (LocalTime)->Unit
) {
    TimePickerDialog(
        onDismissRequest = {
            viewModel.send(InfoIntent.CloseDialog)
        },
        onTimeChange = {
            onTimeChange(it)
            Log.i("TimePicker",it.toString())
            viewModel.send(InfoIntent.CloseDialog)
        },
        modifier = modifier,
        is24HourFormat = true,
        title = { Text(title) }
    )
}