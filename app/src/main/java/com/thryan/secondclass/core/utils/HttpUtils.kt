package com.thryan.secondclass.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class Requests(private val url: String) {
    suspend fun get(block: Request.() -> Unit): String {
        val request = Request().apply {
            block()
        }
        request.builder.url((url + request.api).also(::println))
        request.builder.get()
        val client = OkHttpClient()
        val response: Response = client.newCall(request.builder.build()).awaitResponse()
        return response.awaitString().also(::println)
    }

    suspend fun post(block: Request.() -> Unit): String {
        val request = Request().apply {
            block()
        }

        request.builder.url(url + request.api)
        if (request.requestBody == null) {
            throw Exception("无参数")
        }
        request.builder.post(request.requestBody!!)
        val client = OkHttpClient()
        val response = client.newCall(request.builder.build()).awaitResponse()
        return response.awaitString().also(::println)

    }

    class Request {
        val builder = okhttp3.Request.Builder()
        val api: String
            get() = path + params
        private var params: String = ""
        private var path: String = ""
        var requestBody: RequestBody? = null

        fun path(value: String) {
            this.path = value
        }

        fun params(block: Params.() -> Unit) {
            params = "?" + Params().apply {
                block()
            }.map.entries.joinToString(separator = "&") { "${it.key}=${it.value}" }
        }

        fun headers(block: Headers.() -> Unit) {
            val headers = Headers().apply {
                block()
            }.map.toMap()
            for ((k, v) in headers) {
                println("add $k $v")
                builder.addHeader(k, v)
            }
        }

        fun json(block: JSON.() -> Unit) {
            val jsonObject = JSON().apply {
                block()
            }.jsonObject
            requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"),
                jsonObject.toString().encodeToByteArray()
            )
        }

        fun form(block: Form.() -> Unit) {
            requestBody = Form().apply {
                block()
            }.formBody.build()
        }

    }
}


suspend fun okhttp3.Call.awaitResponse(): Response {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation {
            cancel()
        }
        enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                it.resume(response)
            }
        })
    }
}

suspend fun Response.awaitString(): String {
    val res = this
    return withContext(Dispatchers.IO) {
        res.body()?.string() ?: ""
    }
}

class Form {
    val formBody = FormBody.Builder()
    infix fun String.to(value: String) {
        formBody.add(this, value)
    }
}

class Params {
    val map = mutableMapOf<String, String>()
    infix fun String.to(value: String) {
        map[this] = value
    }
}

class Headers {
    val map = mutableMapOf<String, String>()
    infix fun String.to(value: String) {
        map[this] = value
    }

    fun cookie(block: Cookies.() -> Unit) {
        map["Cookie"] = Cookies().apply {
            block()
        }.map.entries.joinToString(separator = "; ") { "${it.key}=${it.value}" }
    }
}

class Cookies {
    val map = mutableMapOf<String, String>()
    infix fun String.to(value: String) {
        map[this] = value
    }
}


class JSON {
    val jsonObject = JSONObject()
    infix fun String.to(value: String) {
        jsonObject.put(this, value)
    }
}