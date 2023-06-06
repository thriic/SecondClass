package com.thryan.secondclass.core.result

data class SCActivity(
    val id: String,
    val activityStatus: String,
    val activityName: String,
    val startTime: String,
    val endTime: String,
    val isSign: String?,
    val description: String,
    val host: String
)
