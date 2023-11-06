package cn.thriic.common.data

import kotlinx.serialization.Serializable

/**
 * @param id 活动id
 * @param activityStatus 活动状态id 通过成员变量status获取文本
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
 * @param activityType 活动类型id
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
    val activityNum: Int = Int.MAX_VALUE,
    val activityIntegral: Double,
    val activityType: String
) {
    /**
     * 活动类型
     */
    val type: String
        get() = ActivityType.getName(activityType)

    /**
     * 活动状态
     * 0报名中 1待开始 2进行中 3待完结 4完结审核中 5已完结
     */
    val status: String
        get() = ActivityStatus.getName(activityStatus)
}