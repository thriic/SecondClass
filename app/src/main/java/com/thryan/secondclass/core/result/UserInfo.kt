package com.thryan.secondclass.core.result

data class UserInfo(
    val id: String,
    val name: String,
    val sex: Int,
    val score: Double,
    val item: Int,
    val integrity_value: Int,
    val activity: Int
)