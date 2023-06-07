package com.thryan.secondclass.core


import com.thryan.secondclass.core.utils.XMLFactory.toXMLResult
import com.thryan.secondclass.core.result.XMLResult
import com.thryan.secondclass.core.utils.RSAUtils
import com.thryan.secondclass.core.utils.Requests

class Webvpn {
    companion object {
        private val requests = Requests("https://webvpn.cuit.edu.cn/por/")
        suspend fun checkLogin(twfid: String): Boolean {
            val res = requests.post {
                path("svpnSetting.csp?apiversion=1")
                headers {
                    cookie {
                        "ENABLE_RANDCODE" to "0"
                        "TWFID" to twfid
                    }
                }
            }
            return res.contains("auth succ.")
        }
    }

    var twfid: String = ""

    suspend fun auth(): XMLResult = requests.get {
        path("login_auth.csp?apiversion=1")
        headers {
            "Content-Type" to "application/x-www-form-urlencoded"
            cookie {
                "ENABLE_RANDCODE" to "0"
            }
        }
    }.toXMLResult()


    suspend fun login(account: String, password: String): XMLResult {
        val auth = auth()
        return requests.post {
            path("login_psw.csp?anti_replay=1&encrypt=1&apiversion=1")
            headers {
                cookie {
                    "ENABLE_RANDCODE" to "0"
                    "TWFID" to auth.twfid
                }
            }
            form {
                "svpn_req_randcode" to auth.csrf_rand_code
                "svpn_name" to account
                "svpn_password" to RSAUtils.encrypt(
                    "${password}_${auth.csrf_rand_code}",
                    auth.rsa_encrypt_key,
                    "10001"
                )
            }
        }.toXMLResult()
        //登录成功返回radius auth succ
    }


}