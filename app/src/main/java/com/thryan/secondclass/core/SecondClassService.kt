package com.thryan.secondclass.core

import com.google.gson.Gson
import com.thryan.secondclass.core.result.LoginResult
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SecondClassService {

    @Headers("content-type: application/json")
    @POST("api/login?sf_request_type=ajax")
    suspend fun login(
        @Header("sdp-app-session") twfid: String,
        @Body body: RequestBody
    ):LoginResult

    companion object {
        fun create(): SecondClassService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://ekty-cuit-edu-cn.webvpn.cuit.edu.cn:8118/")
                .build()

            return retrofit.create(SecondClassService::class.java);
        }


    }
}