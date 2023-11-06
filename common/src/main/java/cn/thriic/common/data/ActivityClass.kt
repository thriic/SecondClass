package cn.thriic.common.data

import kotlinx.serialization.Serializable

@Serializable
data class ActivityClass(val classifyName: String, val id: String, val minIntegralSchool: Double)
