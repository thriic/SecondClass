package cn.thriic.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Rows<T>(val rows: List<T>)