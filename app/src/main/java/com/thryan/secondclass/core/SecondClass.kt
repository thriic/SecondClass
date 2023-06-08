package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.Result
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.JSONFactory
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

class SecondClass(val twfid: String) {

    private val requests =
        Requests("http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/", JSONFactory())
    var token: String = ""

    /**
     * 登录第二课堂
     * @param account 学号
     * @param password 密码,默认为123456
     * @return JSONResult(message="...",data="token")
     */
    suspend fun login(account: String, password: String = "123456"): Result<String> =
        requests
            .post {
                path("login?sf_request_type=ajax")
                headers {
                    "sdp-app-session" to twfid
                }
                json {
                    "account" to account
                    "password" to password
                }
            }
            .solve {
                success {
                    it
                }
                failure {
                    ""
                }
            }


    /**
     * 获取用户信息
     * @return User
     */
    suspend fun getUser(): Result<User> =
        requests
            .get<JSONObject> {
                path("getLoginUser?sf_request_type=ajax")
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
            }
            .solve {
                success {
                    User(
                        it.getString("id"),
                        it.getString("name"),
                        it.getInt("sex"),
                        it.getJSONObject("loginEmpInfo").getString("orgName")
                    )
                }
                failure {
                    it
                }
            }


    /**
     * 获取活动
     * @return 活动list
     */
    suspend fun getActivities(): Result<List<SCActivity>> =
        requests.get<JSONObject> {
            path("activityInfo/page?activityName=&activityStatus=&activityLx=&activityType=&pageSize=50&sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }
            .solve {
                success {
                    val rows = it.getJSONArray("rows")
                    buildList {
                        for (i in 0 until rows.length()) {
                            val element = rows.getJSONObject(i)
                            add(
                                SCActivity(
                                    element.getString("id"),
                                    element.getString("activityStatus"),
                                    element.getString("activityName"),
                                    element.getString("startTime"),
                                    element.getString("endTime"),
                                    element.getString("isSign"),
                                    element.getString("activityDec"),
                                    element.getString("activityHost")
                                )
                            )
                        }
                    }
                }
                failure { it }
            }


    /**
     * 获取用户积分，活动，诚信值
     * @param user 用户
     * @return ScoreInfo
     */
    suspend fun getScoreInfo(user: User): Result<ScoreInfo> =
        requests
            .get<JSONObject> {
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
            .solve {
                success {
                    ScoreInfo(
                        it.get("score") as BigDecimal,
                        it.getInt("item"),
                        it.getInt("integrity_value"),
                        it.getInt("activity")
                    )
                }
                failure { it }
            }


    suspend fun sign(activity: SCActivity): Result<String> =
        requests
            .post {
                path("activityInfoSign/add?sf_request_type=ajax")
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
                json {
                    "activityId" to activity.id
                }
            }
            .solve {
                success { it }
                failure { it }
            }


    /**
     * 获取活动签到信息
     * @param activity 活动
     * @return SignInfo
     */
    suspend fun getSignInfo(activity: SCActivity): Result<SignInfo> =
        requests
            .get<JSONObject> {
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
            .solve {
                success {
                    val data = it.getJSONArray("rows").getJSONObject(0)
                    SignInfo(
                        it.getString("id"),
                        it.has("signOutTime") && data.has("signInTime")
                    )
                }
                failure { it }
            }


    /**
     * 签到签退
     * @param activity 活动
     * @param signInfo 签到信息
     */
    suspend fun signIn(activity: SCActivity, signInfo: SignInfo): Result<String> =
        requests
            .post {
                path("activityInfoSign/edit?sf_request_type=ajax")
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
                json {
                    "id" to signInfo.signId
                    "signInTime" to activity.startTime.after(10)
                    "signOutTime" to activity.endTime.before(10)
                }
            }
            .solve {
                success { it }
                failure { it }
            }


    /**
     * 获取用户参与的活动
     * @return 活动list
     */
    suspend fun getMyActivities(): Result<List<SCActivity>> =
        requests
            .get<JSONArray> {
                path("activityInfo/my?sf_request_type=ajax")
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
            }
            .solve {
                success {
                    buildList {
                        for (i in 0 until it.length()) {
                            if (it.isNull(i)) continue
                            val element = it.getJSONObject(i)
                            add(
                                SCActivity(
                                    element.getString("id"),
                                    element.getString("activityStatus"),
                                    element.getString("activityName"),
                                    element.getString("startTime"),
                                    element.getString("endTime"),
                                    null,
                                    element.getString("activityDec"),
                                    element.getString("activityHost")
                                )
                            )
                        }
                    }
                }
                failure { it }
            }

    }