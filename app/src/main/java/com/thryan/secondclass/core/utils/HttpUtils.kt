package com.thryan.secondclass.core.utils

import com.thryan.secondclass.core.result.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class Requests(val url: String, val factory: Factory) {

    suspend inline fun <reified T> get(block: Request.() -> Unit): HttpResult<T> {
        val request = Request().apply {
            block()
        }
        request.builder.url((url + request.api).also(::println))
        request.builder.get()
        val client = OkHttpClient()
        val response: Response = client.newCall(request.builder.build()).awaitResponse()
        return factory.convert(response.awaitString().also(::println))
    }

    @JvmName("getString")
    suspend fun get(block: Request.() -> Unit): HttpResult<String> {
        return this.get<String>(block)
    }

    suspend inline fun <reified T> post(block: Request.() -> Unit): HttpResult<T> {
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
        return factory.convert(response.awaitString().also(::println))
    }

    @JvmName("postString")
    suspend fun post(block: Request.() -> Unit): HttpResult<String> {
       return this.post<String>(block)
    }

    inner class Request {
        val builder = okhttp3.Request.Builder()
        private var params: String = ""
        private var path: String = ""
        val api: String
            get() = path + params
        var requestBody: RequestBody? = null


        fun path(path: String) {
            this.path = path
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
            println(jsonObject.toString())
            requestBody = jsonObject.toString().encodeToByteArray()
                .toRequestBody(
                    "application/json;charset=UTF-8".toMediaTypeOrNull(),
                    0
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
        res.body?.string() ?: ""
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


