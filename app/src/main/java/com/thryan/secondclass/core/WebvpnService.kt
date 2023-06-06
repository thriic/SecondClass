package com.thryan.secondclass.core


import com.thryan.secondclass.core.result.WebvpnResult
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.*

interface WebvpnService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("por/login_auth.csp?apiversion=1")
    suspend fun auth(
        @Header("Cookie") captcha: String
    ): WebvpnResult

    @FormUrlEncoded
    @POST("por/login_psw.csp?anti_replay=1&encrypt=1&apiversion=1")
    suspend fun login(
        @Header("Cookie") cookie: String,
        @Field("svpn_req_randcode") randcode: String,
        @Field("svpn_name") account: String = "",
        @Field("svpn_password") pwd: String = "",
        @Field("svpn_rand_code") captcha: String = "",
    ): WebvpnResult

    companion object {
        fun create(): WebvpnService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(XMLConverterFactory.create())
                .baseUrl("https://webvpn.cuit.edu.cn/")
                .build()

            return retrofit.create(WebvpnService::class.java);
        }
    }

}