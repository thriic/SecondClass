package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

/**
 * @param msg 执行签到签退后返回的信息
 * @param code 忘了
 */
@Serializable
class SignResult(val msg: String, val code: String)