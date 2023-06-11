package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class SCActivity(
    val id: String,
    val activityStatus: String,
    val activityName: String,
    val startTime: String,
    val endTime: String,
    val isSign: String = "1",
    val activityDec: String,
    val activityHost: String,
    val signNum: Int,
)

