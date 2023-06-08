package com.thryan.secondclass.core.result


abstract class IResult()
data class Result<T>(
    val success: Boolean,
    val message: String,
    val result: SuccessResult<T>? = null
) : IResult()

data class SuccessResult<T>(val value: T) : IResult()
data class FailureResult(val message: String) : IResult()