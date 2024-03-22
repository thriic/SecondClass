package com.thryan.secondclass

private val majors =
    "大气科学应用气象电子信息工电子信息科学与技通信工微电子科学与工集成电路设计与集成系地理信息科测绘工遥感科学与技环境工环境科应用物理电子科学与技光电信息科学与工气象技术与工机械电子工电气工程及其自动自动机器人工计算机科学与技数字媒体技智能科学与技软件工空间信息与数字技数据科学与大数据技网络工信息安物联网工网络空间安数学区块链工人工智工程管统计经济统计金融工会计财务管英供应链管翻国际经济与贸市场营财务管人力资源管旅游管物流管电子商社会工中国语言文学视觉传达设计"

fun containsClasses(text: String): Boolean {
    val regex = "(\\D{2})(?=(2\\d{2}))"
    val matchResult = Regex(regex).find(text)
    if (matchResult?.groups != null && matchResult.groups[0] != null) {
        val newRegex = matchResult.groups[0]!!.value.map { "$it.*" }.joinToString("")
        return Regex(newRegex).containsMatchIn(majors)
    } else {
        return false
    }
}