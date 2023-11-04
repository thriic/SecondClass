package com.thryan.secondclass.ui.info

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    title: String,
    initialDate: LocalDate,
    onClose: () -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    DatePickerDialog(
        onDismissRequest =  onClose,
        onDateChange = {
            onDateChange(it)
            Log.i("DatePicker", it.toString())
            onClose()
        },
        modifier = modifier,
        title = { Text(title) },
        initialDate = initialDate,
        highlightToday = true
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    title: String,
    initialTime: LocalTime,
    onClose: () -> Unit,
    onTimeChange: (LocalTime) -> Unit
) {
    TimePickerDialog(
        onDismissRequest = onClose,
        onTimeChange = {
            onTimeChange(it)
            Log.i("TimePicker", it.toString())
            onClose()
        },
        modifier = modifier,
        is24HourFormat = true,
        title = { Text(title) },
        initialTime = initialTime
    )
}