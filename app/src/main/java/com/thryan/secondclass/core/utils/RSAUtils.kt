package com.thryan.secondclass.core.utils

import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

object RSAUtils {
    fun encrypt(plaintext: String, rsaEncryptKey: String, rsaEncryptExp: String): String {
        val modulus = BigInteger(rsaEncryptKey, 16)
        val exp = BigInteger(rsaEncryptExp, 16)
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(RSAPublicKeySpec(modulus, exp))
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val ret = BigInteger(cipher.doFinal(plaintext.toByteArray())).toString(16)
        if(ret.contains("-")) return encrypt(plaintext, rsaEncryptKey, rsaEncryptExp)
        return ret
    }

}