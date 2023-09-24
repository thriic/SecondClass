package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class ActivityClass(val classifyName: String, val id: String, val minIntegralSchool: Double)
