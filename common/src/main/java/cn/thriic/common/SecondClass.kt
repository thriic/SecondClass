package cn.thriic.common

import cn.thriic.common.data.ActivityClass
import cn.thriic.common.data.HttpResult
import cn.thriic.common.data.PageInfo
import cn.thriic.common.data.Rows
import cn.thriic.common.data.SCActivity
import cn.thriic.common.data.ScoreDetails
import cn.thriic.common.data.ScoreInfo
import cn.thriic.common.data.SignInfo
import cn.thriic.common.data.SignResult
import cn.thriic.common.data.User
import cn.thriic.common.utils.Requests
import cn.thriic.common.utils.ResponseType
import cn.thriic.common.utils.after
import cn.thriic.common.utils.before


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

    /**
     * 获取活动
     *
     * 按照参数返回满足条件的活动的倒序列表(从新到旧)
     *
     * @param pageNo 页数
     * @param pageSize 一页包含的活动数量
     * @param activityName 活动关键词 用于搜索活动
     * @param activityStatus 活动状态id 默认为空
     * @param activityType 活动类型id
     * @see getMyActivities
     * @return 活动页
     */
    suspend fun getActivities(
        pageNo: Int = 1,
        pageSize: Int = 5,
        activityName: String = "",
        activityStatus: String = "",
        activityType: String = ""
    ): HttpResult<PageInfo> =
        requests
            .get<PageInfo> {
                path =
                    "activityInfo/page?activityName=$activityName&activityStatus=$activityStatus&activityType=$activityType&pageSize=$pageSize&pageNo=$pageNo&sf_request_type=ajax"
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
            }


    /**
     * 获取用户积分，活动，诚信值
     *
     * 需要先通过[getUser]方法获取用户
     *
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
     *
     * 活动id与签到所用id不同
     *
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
     *
     * 可指定签到签退时间（听起来很离谱，但在成信大，这很正常）
     *
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
     * 获取活动类型
     *
     * 理论上为固定值
     *
     * @return 各活动类型的名字与及格分数
     */
    suspend fun getActivityClass(): HttpResult<Rows<ActivityClass>> = requests
        .get<Rows<ActivityClass>> {
            path = "bsActivityClassify/page?sf_request_type=ajax"
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
        }

    /**
     * 获取各类型活动的具体分数
     * @param user 用户
     */
    suspend fun getScoreDetail(user: User): HttpResult<ScoreDetails> = requests
        .get<ScoreDetails> {
            path = "studentScore/appData"
            headers {
                "sdp-app-session" to twfid
                "Authorization" to "Bearer $token"
            }
            params {
                "userId" to user.id
                "sf_request_type" to "ajax"
            }
        }


    /**
     * 获取用户参与的活动
     *
     * 与[getActivities]方法不同,无页数等参数，一次性返回所有报名过的活动的正序列表(从旧到新)
     *
     * 且返回的[SCActivity]类型对象中isSign默认为1
     *
     * @see getActivities
     * @param activityStatus 活动类型id 默认为空
     * @return 活动list
     */
    //http://ekt-cuit-edu-cn.webvpn.cuit.edu.cn:8118/api/activityInfo/my?activityStatus=&sf_request_type=ajax
    suspend fun getMyActivities(activityStatus: String = ""): HttpResult<List<SCActivity>> =
        requests
            .get<List<SCActivity>> {
                path =
                    "activityInfo/my?activityStatus=$activityStatus&sf_request_type=ajax"
                headers {
                    "sdp-app-session" to twfid
                    "Authorization" to "Bearer $token"
                }
            }

    /**
     * TODO
     * 获取学籍信息
     * @param id 学号
     */
    suspend fun getStudentInfo(id: String) = requests.get {
        path = "student/page?studentNo=$id&sf_request_type=ajax"
        headers {
            "sdp-app-session" to twfid
            "Authorization" to "Bearer $token"
        }
    }

}