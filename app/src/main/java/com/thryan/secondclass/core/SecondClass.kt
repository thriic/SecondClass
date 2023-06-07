package com.thryan.secondclass.core

import com.thryan.secondclass.core.utils.JSONFactory.toJSONResult
import com.thryan.secondclass.core.result.JSONResult
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.result.SignResult
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

class SecondClass(val twfid: String) {
    private val requests = Requests("http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/")
    var token: String = ""

    /**
     * 登录第二课堂
     * @param account 学号
     * @param password 密码,默认为123456
     * @return JSONResult(message="...",data="token")
     */
    suspend fun login(account: String, password: String = "123456"): JSONResult<String> =
        requests.post {
            path("login?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
            }
            json {
                "account" to account
                "password" to password
            }
        }.toJSONResult()


    /**
     * 获取用户信息
     * @return User
     */
    suspend fun getUser(): User {
        println(twfid)
        println(token)
        val res = requests.get {
            path("getLoginUser?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }.toJSONResult<JSONObject>()
        val data = res.data
        return User(
            data.getString("id"),
            data.getString("name"),
            data.getInt("sex"),
            data.getJSONObject("loginEmpInfo").getString("orgName")
        )
    }

    /**
     * 获取活动
     * @return 活动list
     */
    suspend fun getActivities(): List<SCActivity> {
        val res = requests.get {
            path("activityInfo/page?activityName=&activityStatus=&activityLx=&activityType=&pageSize=50&sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }.toJSONResult<JSONObject>()
        val rows = res.data.getJSONArray("rows")
        return buildList {
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

    /**
     * 获取用户积分，活动，诚信值
     * @param user 用户
     * @return ScoreInfo
     */
    suspend fun getScoreInfo(user: User): ScoreInfo {
        val res = requests.get {
            path("studentScore/appDataInfo")
            params {
                "userId" to user.id
                "sf_request_type" to "ajax"
            }
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }.toJSONResult<JSONObject>()
        val data = res.data//{score: 3, item: 0, integrity_value: 70, activity: 2}
        return ScoreInfo(
            data.get("score") as BigDecimal,
            data.getInt("item"),
            data.getInt("integrity_value"),
            data.getInt("activity")
        )
    }

    suspend fun sign(activity: SCActivity): SignResult {
        val res = requests.post {
            path("activityInfoSign/add?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
            json {
                "activityId" to activity.id
            }
        }.toJSONResult<JSONObject>()
        val data = res.data
        return SignResult(data.getString("msg"),data.getInt("code"))
    }


    /**
     * 获取活动签到信息
     * @param activity 活动
     * @return SignInfo
     */
    suspend fun getSignInfo(activity: SCActivity): SignInfo {
        val res = requests.get {
            path("activityInfoSign/my")
            params {
                "activityId" to activity.id
                "sf_request_type" to "ajax"
            }
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }.toJSONResult<JSONObject>()
        val data = res.data.getJSONArray("rows").getJSONObject(0)
        return SignInfo(
            data.getString("id"),
            data.has("signOutTime") && data.has("signInTime")
        )
    }

    /**
     * 签到签退
     * @param activity 活动
     * @param signInfo 签到信息
     */
    suspend fun signIn(activity: SCActivity, signInfo: SignInfo): JSONResult<String> {
        val res = requests.post {
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
        }.toJSONResult<String>()
        return res
    }

    /**
     * 获取用户参与的活动
     * @return 活动list
     */
    suspend fun getMyActivities(): List<SCActivity> {
        val res = requests.get {
            path("activityInfo/my?sf_request_type=ajax")
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }.toJSONResult<JSONArray>()
        val rows = res.data
        return buildList {
            for (i in 0 until rows.length()) {
                if(rows.isNull(i)) continue
                val element = rows.getJSONObject(i)
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
}