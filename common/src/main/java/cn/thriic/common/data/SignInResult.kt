package cn.thriic.common.data

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(val message: String = "请求成功")