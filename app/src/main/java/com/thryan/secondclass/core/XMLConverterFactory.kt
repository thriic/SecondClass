package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.WebvpnResult
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.RuntimeException
import java.lang.reflect.Type

class XMLConverterFactory : Converter.Factory() {

    companion object {
        fun create(): XMLConverterFactory {
            return XMLConverterFactory()
        }
    }
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return XMLResponseBodyConverter()
    }

    class XMLResponseBodyConverter : Converter<ResponseBody, WebvpnResult> {

        override fun convert(value: ResponseBody): WebvpnResult {
            try {
                val v = value.string()
                val message = if(v.contains("Message")) v.substringAfter("<Message>").substringBefore("</Message>") else ""
                val csrf_rand_code = if(v.contains("CSRF_RAND_CODE")) v.substringAfter("<CSRF_RAND_CODE>").substringBefore("</CSRF_RAND_CODE>") else ""
                val rsa_encrypt_key = if(v.contains("RSA_ENCRYPT_KEY")) v.substringAfter("<RSA_ENCRYPT_KEY>").substringBefore("</RSA_ENCRYPT_KEY>") else ""
                val twfid = if(v.contains("TwfID")) v.substringAfter("<TwfID>").substringBefore("</TwfID>") else ""
                return WebvpnResult(message, twfid, csrf_rand_code, rsa_encrypt_key)
            } catch (e:Exception) {
                throw RuntimeException("")
            }
        }
    }
}
