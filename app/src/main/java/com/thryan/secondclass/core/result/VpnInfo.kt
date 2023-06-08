package com.thryan.secondclass.core.result

data class VpnInfo(
    val twfid: String,
    val csrf_rand_code: String,
    val rsa_encrypt_key: String
)
