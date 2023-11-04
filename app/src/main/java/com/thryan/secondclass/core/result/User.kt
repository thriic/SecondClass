package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable
import com.thryan.secondclass.core.SecondClass

/**
 * @see SecondClass.getUser
 * @param id 用户id
 * @param name 用户名
 * @param sex 性别(男1女0?)
 */
@Serializable
data class User(val id: String, val name: String, val sex: Int)