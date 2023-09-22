package com.thryan.secondclass.core

import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.result.PageInfo
import com.thryan.secondclass.core.result.Rows
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.result.SignResult
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.Requests
import com.thryan.secondclass.core.utils.ResponseType
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before

class SecondClass(private val twfid: String, var token: String = "") {

    private val requests by lazy {
        Requests("http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/", ResponseType.JSON)
    }


    /**
     * 登录第二课堂
     * @param account 学号
     * @param password 密码,默认为123456
     * @return token
     */
    suspend fun login(account: String, password: String = "123456"): HttpResult<String> = requests
        .post {
            path = "login?sf_request_type=ajax"
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
     * @return User 用户信息
     */
    suspend fun getUser(): HttpResult<User> = requests
        .get<User> {
            path = "getLoginUser?sf_request_type=ajax"
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }

    // activityInfo/page?activityName=&activityStatus=&activityLx=&activityType=&pageSize=50&sf_request_type=ajax&pageNo=2
    /**
     * 获取指定页数的活动
     * @param pageNo 页数
     * @param pageSize 一页包含的活动数量
     * @param activityName 活动关键词 用于搜索活动
     * @return 活动页
     */
    suspend fun getActivities(
        pageNo: Int = 1,
        pageSize: Int = 5,
        activityName: String = ""
    ): HttpResult<PageInfo> =
        requests
            .get<PageInfo> {
                path =
                    "activityInfo/page?activityName=$activityName&pageSize=$pageSize&pageNo=$pageNo&sf_request_type=ajax"
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
            path = "studentScore/appDataInfo"
            params {
                "userId" to user.id
                "sf_request_type" to "ajax"
            }
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


    /**
     * 报名活动 报名中，待开始，进行中的活动均可报名
     * @param activity 活动
     * @return SignResult
     */
    suspend fun sign(activity: SCActivity): HttpResult<SignResult> = requests
        .post<SignResult> {
            path = "activityInfoSign/add?sf_request_type=ajax"
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
     * 活动id与签到所用id不同
     * @param activity 活动
     * @return SignInfo
     */
    suspend fun getSignInfo(activity: SCActivity): HttpResult<Rows<SignInfo>> = requests
        .get<Rows<SignInfo>> {
            path = "activityInfoSign/my"
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
     * 可指定签到签退时间（听起来很离谱，但在成信大，这很正常）
     * @param activity 活动
     * @param signInfo 签到信息
     * @param signInTime 签到时间
     * @param signOutTime 签退时间
     */
    suspend fun signIn(
        activity: SCActivity,
        signInfo: SignInfo,
        signInTime: String = activity.startTime.after(10),
        signOutTime: String = activity.endTime.before(10)
    ): HttpResult<String> = requests
        .post {
            path = "activityInfoSign/edit?sf_request_type=ajax"
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
            json {
                "id" to signInfo.id
                "signInTime" to signInTime
                "signOutTime" to signOutTime
            }
        }


    /**
     * 获取用户参与的活动
     * @return 活动list
     */
    suspend fun getMyActivities(): HttpResult<List<SCActivity>> = requests
        .get<List<SCActivity>> {
            path = "activityInfo/my?sf_request_type=ajax"
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }


}