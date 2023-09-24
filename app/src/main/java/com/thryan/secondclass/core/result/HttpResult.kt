package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class HttpResult<T>(val message: String = "请求成功", val data: T)

fun <T> HttpResult<T>.isSuccess() = message == "请求成功"