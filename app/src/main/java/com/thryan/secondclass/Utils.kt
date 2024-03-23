package com.thryan.secondclass

private const val majors =
    "大气科学应用气象学电子信息工程电子信息科学与技术通信工程微电子科学与工程集成电路设计与集成系统地理信息科学测绘工程遥感科学与技术环境工程环境科学应用物理学电子科学与技术光电信息科学与工程气象技术与工程机械电子工程电气工程及其自动化自动化机器人工程计算机科学与技术数字媒体技术智能科学与技术软件工程空间信息与数字技术数据科学与大数据技术网络工程信息安全物联网工程网络空间安全数学类区块链工程人工智能工程管理统计学经济统计学金融工程会计学财务管理英语供应链管理翻译国际经济与贸易市场营销财务管理人力资源管理旅游管理物流管理电子商务社会工作中国语言文学类视觉传达设计"

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