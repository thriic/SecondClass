package com.thryan.secondclass.core

import com.thryan.secondclass.core.utils.Solve
import com.thryan.secondclass.core.result.Result


class HttpResult<T>(val message: String, val result: T) {
    fun <U> solve(expect: String = "请求成功", block: Solve<T, U>.() -> Unit): Result<U> {
        val solve = Solve<T, U>().apply {
            block()
        }
        val success = expect == message || message.contains(expect)
        return if (success)
            Result(
                true,
                solve.onFailure?.let { it(message) } ?: message,
                solve.onSuccess?.let { it(result) } ?: throw Exception()
            )
        else
            Result(
                false,
                solve.onFailure?.let { it(message) } ?: message
            )

    }
}
