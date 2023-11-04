package com.thryan.secondclass.core.utils


import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun String.after(second: Int): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    val oneHourBefore = dateTime.plusSeconds(second.toLong())
    return oneHourBefore.format(formatter)
}

fun String.before(second: Int): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    val oneHourBefore = dateTime.minusSeconds(second.toLong())
    return oneHourBefore.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}


fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    return dateTime.toLocalDate()
}

fun String.toLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    return dateTime.toLocalTime()
}

fun LocalDateTime.formatDate(): String = this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
fun LocalDateTime.formatTime(): String = this.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
fun LocalDateTime.formatDateTime(): String =
    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

