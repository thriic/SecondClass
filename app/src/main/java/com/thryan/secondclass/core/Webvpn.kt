package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.VpnInfo
import com.thryan.secondclass.core.result.Result
import com.thryan.secondclass.core.utils.RSAUtils
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.XMLFactory

class Webvpn {
    companion object {
        private val requests = Requests("https://webvpn.cuit.edu.cn/por/", XMLFactory())


        suspend fun checkLogin(twfid: String): Result<Boolean> =
            requests
                .post<VpnInfo> {
                    path("svpnSetting.csp?apiversion=1")
                    headers {
                        cookie {
                            "ENABLE_RANDCODE" to "0"
                            "TWFID" to twfid
                        }
                    }
                }
                .solve("auth succ.") {
                    success { true }
                    failure { it }
                }
    }

    var twfid: String = ""


    suspend fun auth(): Result<VpnInfo> =
        requests
            .get<VpnInfo> {
                path("login_auth.csp?apiversion=1")
                headers {
                    "Content-Type" to "application/x-www-form-urlencoded"
                    cookie {
                        "ENABLE_RANDCODE" to "0"
                    }
                }
            }
            .solve("login auth success") {
                success { it }
                failure { it }
            }


    suspend fun login(account: String, password: String): Result<String> {
        val auth = auth()
        if (!auth.success) return Result(false, auth.message)
        val vpnInfo = auth.result!!.value
        return requests
            .post<VpnInfo> {
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
            .solve("radius auth succ") {
                success { it.twfid }
                failure { it }
            }
    }

}