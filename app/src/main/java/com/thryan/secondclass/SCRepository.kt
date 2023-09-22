package com.thryan.secondclass

import android.util.Log
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.result.SignResult
import com.thryan.secondclass.core.result.UserInfo
import com.thryan.secondclass.core.result.plus
import com.thryan.secondclass.core.result.success
import kotlinx.coroutines.flow.MutableStateFlow

class SCRepository {

    val activities: MutableStateFlow<List<SCActivity>> = MutableStateFlow(emptyList())
    private var secondClass: SecondClass? = null
    private var account: String? = null
    private var password: String? = null


    fun getActivity(id: String): SCActivity? {
        activities.value.forEach {
            if (id == it.id) return it
        }
        return null
    }

    suspend fun setActivity(id: String, isSign: String) {
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
        if (msg.success()) return true
        else throw Exception(msg.message)
    }


    suspend fun getUserInfo(): UserInfo {
        val user = secondClass!!.getUser().data
        val scoreInfo = secondClass!!.getScoreInfo(user).data
        return user + scoreInfo
    }

    suspend fun getActivities(
        pageNo: Int = 1,
        pageSize: Int = 5,
        keyword: String = "",
        clear: Boolean = false
    ): Int {
        val res = secondClass!!.getActivities(pageNo, pageSize, keyword)
        if (res.success()) {
            activities.emit(if (clear) res.data.rows else activities.value + res.data.rows)
            return res.data.rows.size
        } else throw Exception(res.message)
    }


    suspend fun getSignInfo(activity: SCActivity): List<SignInfo> {
        val res = secondClass!!.getSignInfo(activity)
        return res.data.rows
    }

    suspend fun sign(activity: SCActivity): HttpResult<SignResult> {
        val res = secondClass!!.sign(activity)
        if (res.success() && res.data.code == "1") {
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
        if (res.success()) {
            return res.data
        } else throw Exception(res.message)
    }
}