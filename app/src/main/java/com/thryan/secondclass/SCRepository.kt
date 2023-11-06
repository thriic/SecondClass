package com.thryan.secondclass

import android.util.Log
import cn.thriic.common.SecondClass
import cn.thriic.common.data.ActivityClass
import cn.thriic.common.data.ActivityStatus
import cn.thriic.common.data.ActivityType
import cn.thriic.common.data.HttpResult
import cn.thriic.common.data.Rows
import cn.thriic.common.data.SCActivity
import cn.thriic.common.data.ScoreDetails
import cn.thriic.common.data.ScoreInfo
import cn.thriic.common.data.SignInfo
import cn.thriic.common.data.SignResult
import cn.thriic.common.data.User
import cn.thriic.common.data.isSuccess
import com.thryan.secondclass.ui.page.FilterState
import com.thryan.secondclass.ui.user.RadarScore
import kotlinx.coroutines.flow.MutableStateFlow

class SCRepository {

    val activities: MutableStateFlow<List<SCActivity>> = MutableStateFlow(emptyList())
    private var secondClass: SecondClass? = null
    private var account: String? = null
    private var password: String? = null

    private var user: User? = null

    var radarScores: List<RadarScore>? = null
    var scoreInfo: ScoreInfo? = null
    var onlySign: Boolean? = null
    var myActivities: List<SCActivity>? = null


    fun getActivity(id: String): SCActivity? {
        activities.value.forEach {
            if (id == it.id) return it
        }
        return null
    }

    suspend fun updateActivitySign(id: String, isSign: String) {
        activities.emit(activities.value.map {
            if (it.id == id) return@map it.copy(isSign = isSign)
            else return@map it
        })
    }

    fun init(twfid: String, account: String, password: String?) {
        this.secondClass = SecondClass(twfid)
        this.account = account
        this.password = if (password.isNullOrEmpty()) "123456" else password
    }

    suspend fun login(): Boolean {
        Log.i("SCRepository", "$secondClass $account $password")
        val msg = secondClass!!.login(account!!, password!!)
        if (msg.isSuccess()) return true
        else throw Exception(msg.message)
    }


    suspend fun getActivities(
        pageNo: Int = 1,
        pageSize: Int = 5,
        filter: FilterState,
        clear: Boolean = false
    ): Int {
        if (filter.onlySign) {
            if (this.onlySign != true || this.myActivities == null || this.myActivities!!.isEmpty() || this.myActivities!!.first().status != filter.status) {
                this.onlySign = true
                val res =
                    secondClass!!.getMyActivities(activityStatus = ActivityStatus.getId(filter.status))
                if (res.isSuccess()) {
                    myActivities = res.data.reversed()
                } else throw Exception(res.message)
            }
            val typeId = ActivityType.getId(filter.type)
            activities.emit(myActivities!!.filter {
                (filter.keyword == "" || it.activityName.contains(filter.keyword)) && (typeId == "" || typeId == it.activityType)
            })
            return myActivities!!.size
        } else {
            this.onlySign = false
            val res = secondClass!!.getActivities(
                pageNo,
                pageSize,
                activityName = filter.keyword,
                activityStatus = ActivityStatus.getId(filter.status),
                activityType = ActivityType.getId(filter.type)
            )
            if (res.isSuccess()) {
                activities.emit(if (clear) res.data.rows else activities.value + res.data.rows)
                return res.data.rows.size
            } else throw Exception(res.message)
        }
    }


    suspend fun getSignInfo(activity: SCActivity): List<SignInfo> {
        val res = secondClass!!.getSignInfo(activity)
        return res.data.rows
    }

    suspend fun sign(activity: SCActivity): HttpResult<SignResult> {
        val res = secondClass!!.sign(activity)
        if (res.isSuccess() && res.data.code == "1") {
            return res
        } else throw Exception(res.data.msg)
    }

    suspend fun signIn(
        activity: SCActivity,
        signInfo: SignInfo,
        signInTime: String,
        signOutTime: String
    ): String {
        val res = secondClass!!.signIn(activity, signInfo, signInTime, signOutTime)
        if (res.isSuccess()) {
            return res.data
        } else throw Exception(res.message)
    }

    suspend fun getScoreInfo(): ScoreInfo {
        if (scoreInfo != null) return scoreInfo!!
        if (user == null) throw Exception("获取获取用户信息失败")
        val res = secondClass!!.getScoreInfo(user!!)
        if (res.isSuccess()) {
            scoreInfo = res.data
            return res.data
        } else throw Exception(res.message)
    }

    suspend fun getUser(): User {
        if (user != null) return user!!
        val res = secondClass!!.getUser()
        if (res.isSuccess()) {
            user = res.data
            return res.data
        } else throw Exception(res.message)
    }

    suspend fun getActivityClass(): Rows<ActivityClass> {
        val res = secondClass!!.getActivityClass()
        if (res.isSuccess()) {
            return res.data
        } else throw Exception(res.message)
    }

    suspend fun getScoreDetails(): ScoreDetails {
        if (user == null) throw Exception("获取获取用户信息失败")
        val res = secondClass!!.getScoreDetail(user!!)
        if (res.isSuccess()) {
            return res.data
        } else throw Exception(res.message)
    }
}