package cn.thriic.common.data

import kotlinx.serialization.Serializable
import cn.thriic.common.SecondClass

/**
 * @see SecondClass.getUser
 * @param id 用户id
 * @param name 用户名
 * @param sex 性别(男1女0?)
 */
@Serializable
data class User(val id: String, val name: String, val sex: Int)