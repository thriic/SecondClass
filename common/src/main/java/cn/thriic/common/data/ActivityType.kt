package cn.thriic.common.data

object ActivityType {
    val activityTypeMap: Map<String, String> = mapOf(
        "1427966682442641410" to "思想政治与道德修养",
        "1427970375951007746" to "社会工作与能力拓展",
        "1427970415725592577" to "学术科技与创新创业",
        "1427970448030121986" to "艺术体验与审美修养",
        "1427970491399225346" to "心理素质与身体素质",
        "1427970528921468930" to "社会实践与志愿服务",
        "1439991632602697729" to "传统文化与人文素养",
        "1439991816728449025" to "人际交往与沟通能力"
    )

    fun getName(activityId: String): String {
        return activityTypeMap[activityId] ?: "未知"
    }

    fun getId(activityName: String): String {
        return activityTypeMap.entries.firstOrNull { it.value == activityName }?.key ?: ""
    }
}