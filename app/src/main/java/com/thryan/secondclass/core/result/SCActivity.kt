package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

/**
 * @param id 活动id
 * @param activityStatus 活动状态 0报名中 1待开始 2进行中 3待完结 4完结审核中 5已完结
 * @param activityName 活动名称
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @param isSign 是否报名
 * @param activityDec 活动介绍
 * @param activityHost 主办方
 * @param activityAddress 活动地点
 * @param activityIntegral 活动分数
 * @param activityNum 活动最大报名人数
 * @param signNum 已报名人数
 */
@Serializable
data class SCActivity(
    val id: String,
    val activityStatus: String,
    val activityName: String,
    val startTime: String,
    val endTime: String,
    val signTime: String,
    val isSign: String = "1",
    val activityDec: String,
    val activityHost: String,
    val activityAddress: String,
    val signNum: Int,
    val activityNum: Int = 120,
    val activityIntegral: Double,
)

