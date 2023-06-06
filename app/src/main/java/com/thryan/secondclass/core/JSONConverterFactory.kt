package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.WebvpnResult
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.RuntimeException
import java.lang.reflect.Type

class JSONConverterFactory : Converter.Factory() {
    companion object {
        fun create(): JSONConverterFactory {
            return JSONConverterFactory()
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
                return WebvpnResult("","","","")
            } catch (e:Exception) {
                throw RuntimeException("")
            }
        }
    }
}