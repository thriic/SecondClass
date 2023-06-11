package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

/**
 * @param id 用户id
 * @param name 用户名
 * @param sex 性别(男1女0?)
 * @param orgName 班级名
 */
@Serializable
data class User(val id: String, val name: String, val sex: Int)