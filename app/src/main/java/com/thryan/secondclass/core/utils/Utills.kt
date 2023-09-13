package com.thryan.secondclass.core.utils


import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.result.SignInfo
import java.time.LocalDate
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

fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}


fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    return dateTime.toLocalDate()
}

fun <T> HttpResult<T>.success() = message == "请求成功"

enum class ActivityStatus {
    ENROLLING,       //报名中
    UPCOMING,        //待开始
    ONGOING,         //进行中
    PENDING_FINISH,  //待完结
    FINISHED,        //已完结
    UNKNOWN
}

fun textFromStatus(status: String) =
    when (status) {
        "0" -> "报名中"
        "1" -> "待开始"
        "2" -> "进行中"
        "3" -> "待完结"
        "4" -> "完结审核中"
        "5" -> "已完结"
        else -> "未知"
    }

fun String.getActivityStatus() =
    when (this) {
        "0" -> ActivityStatus.ENROLLING
        "1" -> ActivityStatus.UPCOMING
        "2" -> ActivityStatus.ONGOING
        "3" -> ActivityStatus.PENDING_FINISH
        "5" -> ActivityStatus.FINISHED
        else -> ActivityStatus.UNKNOWN
    }

fun SignInfo.signIn() = this.signInTime.isNotEmpty() && this.signOutTime.isNotEmpty()
