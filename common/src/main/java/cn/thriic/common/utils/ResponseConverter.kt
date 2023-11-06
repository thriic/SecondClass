package cn.thriic.common.utils

import cn.thriic.common.data.VpnInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import cn.thriic.common.data.HttpResult
import cn.thriic.common.data.SignInResult

//这依托shit是谁写的
//是我啊，没事了
class ResponseConverter(val type: ResponseType) {
    inline fun <reified T> convert(value: String): HttpResult<T> =
        when (type) {
            ResponseType.XML -> {
                val message = if (value.contains("Message")) value.substringAfter("<Message>")
                    .substringBefore("</Message>").replace("<![CDATA[", "").replace("]]>", "")
                else throw Exception("Webvpn验证失败")
                val twfid = value.substringAfter("<TwfID>").substringBefore("</TwfID>")

                if (value.contains("CSRF_RAND_CODE") && value.contains("RSA_ENCRYPT_KEY")) {
                    val csrf_rand_code = value.substringAfter("<CSRF_RAND_CODE>")
                        .substringBefore("</CSRF_RAND_CODE>")
                    val rsa_encrypt_key = value.substringAfter("<RSA_ENCRYPT_KEY>")
                        .substringBefore("</RSA_ENCRYPT_KEY>")
                    HttpResult(
                        message,
                        VpnInfo(twfid, csrf_rand_code, rsa_encrypt_key) as T
                    )
                } else {
                    HttpResult(message, twfid as T)
                }
            }

            ResponseType.JSON -> {
                if (value.contains("Server internal error")) throw Exception("500 Server internal error")
                if (value.contains("<html>")) throw Exception("webvpn登录失败")
                val json = Json {
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                }
                try {
                    json.decodeFromString(value.trimIndent())
                } catch (e: Exception) {
                    if (e.message?.contains("Field 'data' is required for type with serial name 'com.thryan.secondclass.core.result.HttpResult'") == true) {
                        val signIn = json.decodeFromString<SignInResult>(value.trimIndent())
                        HttpResult(signIn.message,"" as T)
                    } else throw e
                }
            }

        }

}

enum class ResponseType {
    XML, JSON
}
