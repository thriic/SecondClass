package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.result.Rows
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.result.SignResult
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.Factory
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before

class SecondClass(val twfid: String) {

    private val requests =
        Requests("http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/", Factory("JSON"))
    var token: String = ""

    /**
     * 登录第二课堂
     * @param account 学号
     * @param password 密码,默认为123456
     * @return JSONResult(message="...",data="token")
     */
    suspend fun login(account: String, password: String = "123456"): HttpResult<String> = requests
        .post {
            path("login?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
            }
            json {
                "account" to account
                "password" to password
            }
        }.also { this.token = it.data }


    /**
     * 获取用户信息
     * @return User
     */
    suspend fun getUser(): HttpResult<User> = requests
        .get<User> {
            path("getLoginUser?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


    /**
     * 获取活动
     * @return 活动list
     */
    suspend fun getActivities(): HttpResult<Rows<SCActivity>> = requests
        .get<Rows<SCActivity>> {
            path("activityInfo/page?activityName=&activityStatus=&activityLx=&activityType=&pageSize=50&sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


    /**
     * 获取用户积分，活动，诚信值
     * @param user 用户
     * @return ScoreInfo
     */
    suspend fun getScoreInfo(user: User): HttpResult<ScoreInfo> = requests
        .get<ScoreInfo> {
            path("studentScore/appDataInfo")
            params {
                "userId" to user.id
                "sf_request_type" to "ajax"
            }
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


    suspend fun sign(activity: SCActivity): HttpResult<SignResult> = requests
        .post<SignResult> {
            path("activityInfoSign/add?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
            json {
                "activityId" to activity.id
            }
        }


    /**
     * 获取活动签到信息
     * @param activity 活动
     * @return SignInfo
     */
    suspend fun getSignInfo(activity: SCActivity): HttpResult<Rows<SignInfo>> = requests
        .get<Rows<SignInfo>> {
            path("activityInfoSign/my")
            params {
                "activityId" to activity.id
                "sf_request_type" to "ajax"
            }
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


    /**
     * 签到签退
     * @param activity 活动
     * @param signInfo 签到信息
     */
    suspend fun signIn(activity: SCActivity, signInfo: SignInfo): HttpResult<String> = requests
        .post {
            path("activityInfoSign/edit?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
            json {
                "id" to signInfo.id
                "signInTime" to activity.startTime.after(10)
                "signOutTime" to activity.endTime.before(10)
            }
        }


    /**
     * 获取用户参与的活动
     * @return 活动list
     */
    suspend fun getMyActivities(): HttpResult<List<SCActivity>> = requests
        .get<List<SCActivity>> {
            path("activityInfo/my?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


}