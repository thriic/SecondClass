package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class Rows<T>(val rows:List<T>)