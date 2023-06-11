package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
class SignResult(val msg: String, val code: String)