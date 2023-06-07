package com.thryan.secondclass.core.utils

import com.thryan.secondclass.core.result.JSONResult
import org.json.JSONObject

object JSONFactory {
    inline fun <reified T> convert(value: String): JSONResult<T> {
        if (value.contains("Server internal error")) throw Exception("500 Server internal error")
        val json = JSONObject(value)
        val message = json.getString("message")
        return JSONResult(
            message, when (T::class.java) {
                java.lang.String::class.java -> (if (json.has("data")) json.getString("data") else "") as T
                org.json.JSONObject::class.java -> json.getJSONObject("data") as T
                org.json.JSONArray::class.java -> json.getJSONArray("data") as T
                else -> throw Exception()
            }
        )
    }

    inline fun <reified T> String.toJSONResult() = convert<T>(this)
}