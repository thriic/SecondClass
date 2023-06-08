package com.thryan.secondclass.core.result

import com.thryan.secondclass.core.utils.Solve


class HttpResult<T>(val message: String, val result: T) {
    fun <U> solve(expect: String = "请求成功", block: Solve<T, U>.() -> Unit): Result<U> {
        val solve = Solve<T, U>().apply {
            block()
        }
        val success = expect == message || message.contains(expect)
        return if (success)
            Result(
                success,
                solve.onFailure?.let { it(message) } ?: message,
                SuccessResult(solve.onSuccess?.let { it(result) }
                    ?: throw Exception())
            )
        else
            Result(
                success,
                solve.onFailure?.let { it(message) } ?: message
            )

    }
}
