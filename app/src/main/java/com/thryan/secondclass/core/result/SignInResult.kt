package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(val message: String = "请求成功")