package com.thryan.secondclass.core.result


abstract class IResult
data class Result<T>(
    val success: Boolean,
    val message: String,
    val value: T? = null
) : IResult()
