package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class ScoreDetail(val name: String, val value: Double)

@Serializable
data class ScoreDetails(val activity: List<ScoreDetail>)

