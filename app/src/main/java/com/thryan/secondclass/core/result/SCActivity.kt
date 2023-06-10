package com.thryan.secondclass.core.result

data class SCActivity(
    val id: String,
    val activityStatus: ActivityStatus,
    val activityName: String,
    val startTime: String,
    val endTime: String,
    val isSign: String?,//已报名
    val description: String,
    val host: String,
    val signNum: Int,
)

enum class ActivityStatus {
    ENROLLING,       //报名中
    UPCOMING,        //待开始
    ONGOING,         //进行中
    PENDING_FINISH,  //待完结
    FINISHED,        //已完结
    UNKNOWN
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