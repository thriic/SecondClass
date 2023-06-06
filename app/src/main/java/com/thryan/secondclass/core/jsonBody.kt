package com.thryan.secondclass.core

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

class JSON {
    val bodyMap = HashMap<String, String>()

    infix fun String.to(value: String) {
        bodyMap[this] = value
    }
}

fun json(block: JSON.() -> Unit): RequestBody {
    val gson = Gson()
    val entity = gson.toJson(JSON().apply {
        block()
    }.bodyMap)
    return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), entity)
}
