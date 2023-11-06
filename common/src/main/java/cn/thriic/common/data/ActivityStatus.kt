package cn.thriic.common.data

/**
 * 活动状态
 */
object ActivityStatus {
    val activityStatusMap: Map<String, String> = mapOf(
            "0" to "报名中",
            "1" to "待开始",
            "2" to "进行中",
            "3" to "待完结",
            "4" to "完结审核中",
            "5" to "已完结"
    )

    fun getName(activityId: String): String {
        return activityStatusMap[activityId] ?: "未知"
    }

    fun getId(activityName: String): String {
        return activityStatusMap.entries.firstOrNull { it.value == activityName }?.key ?: ""
    }
}