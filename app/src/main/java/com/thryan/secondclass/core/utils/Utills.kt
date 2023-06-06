package com.thryan.secondclass.core.utils


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.after(minutes: Int): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    val oneHourBefore = dateTime.plusMinutes(minutes.toLong())
    return oneHourBefore.format(formatter)
}

fun String.before(minutes: Int): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    val oneHourBefore = dateTime.minusMinutes(minutes.toLong())
    return oneHourBefore.format(formatter)
}