package com.thryan.secondclass.core.utils

import com.thryan.secondclass.core.result.VpnInfo
import com.thryan.secondclass.core.HttpResult
import org.json.JSONObject

interface Factory {
    fun <T> convert(value: String): HttpResult<T>
}

class JSONFactory : Factory {

    override fun <T> convert(value: String): HttpResult<T> {
        if (value.contains("Server internal error")||value.contains("<html>")) throw Exception("500 Server internal error")
        val json = JSONObject(value)
        val message = json.getString("message")
        return HttpResult(message, (if (!json.isNull("data")) json.get("data") else json) as T)
    }

}

class XMLFactory : Factory {
    override fun <T> convert(value: String): HttpResult<T> {
        val message = if (value.contains("Message")) value.substringAfter("<Message>")
            .substringBefore("</Message>") else ""
        val csrf_rand_code =
            if (value.contains("CSRF_RAND_CODE")) value.substringAfter("<CSRF_RAND_CODE>")
                .substringBefore("</CSRF_RAND_CODE>") else ""
        val rsa_encrypt_key =
            if (value.contains("RSA_ENCRYPT_KEY")) value.substringAfter("<RSA_ENCRYPT_KEY>")
                .substringBefore("</RSA_ENCRYPT_KEY>") else ""
        val twfid =
            if (value.contains("TwfID")) value.substringAfter("<TwfID>")
                .substringBefore("</TwfID>") else ""
        return HttpResult(message, VpnInfo(twfid, csrf_rand_code, rsa_encrypt_key) as T)
    }
}
