package com.thryan.secondclass

import com.thryan.secondclass.core.RSAUtils
import com.thryan.secondclass.core.SecondClassService
import com.thryan.secondclass.core.XMLConverterFactory
import com.thryan.secondclass.core.WebvpnService
import com.thryan.secondclass.core.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import retrofit2.Retrofit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun loginWebvpn(): Unit = runBlocking {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = WebvpnService.create()
                val auth = call.auth("ENABLE_RANDCODE=0")
                println("${auth.csrf_rand_code}${auth.message}${auth.twfid}")

                if (auth.rsa_encrypt_key.isEmpty() || auth.csrf_rand_code.isEmpty()) {

                    println("key或randcode返回空")
                }

                val login = call.login(
                    "ENABLE_RANDCODE=0; TWFID=${auth.twfid}",
                    auth.csrf_rand_code,
                    "2022101063",
                    RSAUtils.encrypt(
                        "thryan0829_${auth.csrf_rand_code}",
                        auth.rsa_encrypt_key,
                        "10001"
                    )
                )
                println(login.message)

            } catch (e: Exception) {
                // 网络返回失败
                println(e)
            }
        }
        job.join()
    }

    @Test
    fun rsa() {
        val plaintext = "Hello World"
        val rsaEncryptKey =
            "AD0A69BD1846B80874DB71617E90D1A538475118E4ED687BA19C516A84D2CB6B2923807C4D3AEBF35FF334FBED5B29E9E0BAC298E893D0BA83D7DCCF558A65D6FD62AA1E29E66B7C0A0A893C0D514949C10623367795537D8C93635EFA8D8EE1008EA673D0B1123526148BC488D3AC104B6E289F5389E7730C2B090F7D026D50ECDF93EE1691859D097C56ECD03065EB693D1815AB129EB711B54DE1B991213D465881AFC63C2D6C4D773E20C8CE4C8E7249415C75BF93B9800610AE0B09E0ACC5989AC1963924F9761E4609A9DCFEC64C4836E99E1CC3E4B2CEAD17AC6D220CB2810FC2C5015925B1D43E0BB61799D4012CB216F88CA6D1DB04867C080699EB"
        val rsaEncryptExp = "65537"
        val publicKey = RSAUtils.encrypt(plaintext, rsaEncryptKey, rsaEncryptExp)
        println(publicKey)
    }

    @Test
    fun loginSC() = runBlocking {
        val job = CoroutineScope(Dispatchers.IO).launch {
            val call = SecondClassService.create()
            val login = call.login("1309e25f18d3d62a", json {
                "account" to "2022101063"
                "password" to "123456"
            })
            println(login)
        }
        job.join()
    }
}