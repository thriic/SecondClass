package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.XMLResult


object XMLFactory {
    fun convert(v: String): XMLResult {
        val message = if (v.contains("Message")) v.substringAfter("<Message>")
            .substringBefore("</Message>") else ""
        val csrf_rand_code = if (v.contains("CSRF_RAND_CODE")) v.substringAfter("<CSRF_RAND_CODE>")
            .substringBefore("</CSRF_RAND_CODE>") else ""
        val rsa_encrypt_key =
            if (v.contains("RSA_ENCRYPT_KEY")) v.substringAfter("<RSA_ENCRYPT_KEY>")
                .substringBefore("</RSA_ENCRYPT_KEY>") else ""
        val twfid =
            if (v.contains("TwfID")) v.substringAfter("<TwfID>").substringBefore("</TwfID>") else ""
        return XMLResult(message, twfid, csrf_rand_code, rsa_encrypt_key)
    }
    fun String.toXMLResult() = convert(this)
}
