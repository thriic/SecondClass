package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.VpnInfo
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.utils.RSAUtils
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.ResponseType

object WebVpn {
    private val requests by lazy { Requests("https://webvpn.cuit.edu.cn/por/", ResponseType.XML) }

    suspend fun checkLogin(twfid: String): Boolean {
        val res = requests.get {
            path = "svpnSetting.csp?apiversion=1"
            headers {
                cookie {
                    "ENABLE_RANDCODE" to "0"
                    "TWFID" to twfid
                }
            }
        }
        return res.message == "auth succ."
    }


    suspend fun auth(): HttpResult<VpnInfo> = requests
        .get<VpnInfo> {
            path = "login_auth.csp?apiversion=1"
            headers {
                "Content-Type" to "application/x-www-form-urlencoded"
                cookie {
                    "ENABLE_RANDCODE" to "0"
                }
            }
        }


    suspend fun login(auth: VpnInfo, account: String, password: String): HttpResult<String> {
        println("account $account pwd $password")
        val res = requests
            .post<String> {
                path = "login_psw.csp?anti_replay=1&encrypt=1&apiversion=1"
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
            }
        return HttpResult(
            when (res.message) {
                "radius auth succ" -> "请求成功"
                "CAPTCHA required" -> "需要验证码，请一段时间后再试"
                "Invalid username or password!" -> "账号或密码错误"
                "maybe attacked" -> "尝试次数过多，一段时间后再试"
                else -> res.message
            }, res.data
        )
        //Invalid username or password

    }

    suspend fun logout(twfid: String) = requests
        .post<String> {
            path = "logout.csp?apiversion=1"
            headers {
                cookie {
                    "TWFID" to twfid
                }
            }
            json { }
        }//logout user success

}