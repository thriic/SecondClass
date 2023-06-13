package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.VpnInfo
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.utils.Factory
import com.thryan.secondclass.core.utils.RSAUtils
import com.thryan.secondclass.core.utils.Requests

object Webvpn {
    private val requests = Requests("https://webvpn.cuit.edu.cn/por/", Factory("XML"))


    suspend fun checkLogin(twfid: String): Boolean {
        val res = requests.get {
            path("svpnSetting.csp?apiversion=1")
            headers {
                cookie {
                    "ENABLE_RANDCODE" to "0"
                    "TWFID" to twfid
                }
            }
        }
        return res.message == "auth succ."
    }


    private suspend fun auth(): HttpResult<VpnInfo> = requests
        .get<VpnInfo> {
            path("login_auth.csp?apiversion=1")
            headers {
                "Content-Type" to "application/x-www-form-urlencoded"
                cookie {
                    "ENABLE_RANDCODE" to "0"
                }
            }
        }


    suspend fun login(account: String, password: String): HttpResult<String> {
        val auth = auth()
        if (auth.message != "login auth success") return HttpResult(auth.message, auth.message)
        val vpnInfo = auth.data!!
        val res = requests
            .post<String> {
                path("login_psw.csp?anti_replay=1&encrypt=1&apiversion=1")
                headers {
                    cookie {
                        "ENABLE_RANDCODE" to "0"
                        "TWFID" to vpnInfo.twfid
                    }
                }
                form {
                    "svpn_req_randcode" to vpnInfo.csrf_rand_code
                    "svpn_name" to account
                    "svpn_password" to RSAUtils.encrypt(
                        "${password}_${vpnInfo.csrf_rand_code}",
                        vpnInfo.rsa_encrypt_key,
                        "10001"
                    )
                }
            }
        return HttpResult(
            when (res.message) {
                "radius auth succ" -> "请求成功"
                else -> res.message
            }, res.data
        )
        //Invalid username or password

    }

}