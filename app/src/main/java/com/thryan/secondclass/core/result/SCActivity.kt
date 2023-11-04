package com.thryan.secondclass.core.result

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
        get() = when (activityType) {
            "1427966682442641410" -> "思想政治与道德修养"
            "1427970375951007746" -> "社会工作与能力拓展"
            "1427970415725592577" -> "学术科技与创新创业"
            "1427970448030121986" -> "艺术体验与审美修养"
            "1427970491399225346" -> "心理素质与身体素质"
            "1427970528921468930" -> "社会实践与志愿服务"
            "1439991632602697729" -> "传统文化与人文素养"
            "1439991816728449025" -> "人际交往与沟通能力"
            else -> "未知"
        }

    /**
     * 活动状态
     * 0报名中 1待开始 2进行中 3待完结 4完结审核中 5已完结
     */
    val status: String
        get() = when (activityStatus) {
            "0" -> "报名中"
            "1" -> "待开始"
            "2" -> "进行中"
            "3" -> "待完结"
            "4" -> "完结审核中"
            "5" -> "已完结"
            else -> "未知"
        }
}

