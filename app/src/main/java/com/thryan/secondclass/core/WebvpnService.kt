package com.thryan.secondclass.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.*
import java.util.*

interface WebvpnService {

    @FormUrlEncoded
    @GET("por/login_auth.csp?apiversion=1")
    fun search(
        @Header("ENABLE_RANDCODE") captcha: Int,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): XMLResult

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): WebvpnService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build()

            return retrofit.create(WebvpnService::class.java);
        }
    }

}