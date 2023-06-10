package com.thryan.secondclass.ui.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.ui.HttpState
import com.thryan.secondclass.ui.HttpStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PageViewModel(navController: NavHostController, private val twfid: String,private val account:String) :
    AViewModel(navController) {
    private val TAG = "PageViewModel"

    private val _uiState = MutableStateFlow(listOf<SCActivity>())
    val uiState: StateFlow<List<SCActivity>> = _uiState.asStateFlow()

    private val _httpState = MutableStateFlow(HttpState(HttpStatus.Pending, "登录中"))
    val httpState: StateFlow<HttpState> = _httpState.asStateFlow()


    private lateinit var user: User
    private lateinit var scoreInfo: ScoreInfo
    private var secondClass: SecondClass = SecondClass(twfid)

    lateinit var snackbarHostState: SnackbarHostState

    init {
        init()
    }

    fun updateActivities(activities: List<SCActivity>) {
        _uiState.update {
            activities
        }
    }

    fun updateHttpState(httpStatus: HttpStatus = HttpStatus.Pending, message: String) {
        _httpState.update {
            it.copy(httpStatus = httpStatus, message = message)
        }
    }


    fun getUserInfo(): User = user

    fun getScoreInfo(): ScoreInfo = scoreInfo

    fun sign(activity: SCActivity) {
        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                updateHttpState(HttpStatus.Pending, "报名中")
                val res = secondClass.sign(activity)
                if (!res.success) throw Exception(res.message)
                if (res.value!! == "报名成功") {
                    updateHttpState(HttpStatus.Success, res.value)
                    this@PageViewModel.getActivities()
                    snackbarHostState.showSnackbar("报名成功")
                }
                else
                    throw Exception(res.value)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                updateHttpState(HttpStatus.Fail, e.toString())
                snackbarHostState.showSnackbar(e.message!!)
            }
        }
    }

    fun signIn(activity: SCActivity) {
        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                updateHttpState(HttpStatus.Pending, "签到中")
                val signInfo = secondClass.getSignInfo(activity)
                if (!signInfo.success) throw Exception(signInfo.message)
                if (signInfo.value!!.isSignIn) throw Exception("勿重复签到")
                val res = secondClass.signIn(activity, signInfo.value)
                if (res.success) {
                    updateHttpState(HttpStatus.Success, res.message)
                    snackbarHostState.showSnackbar("签到成功")
                } else {
                    throw Exception(res.message)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                updateHttpState(HttpStatus.Fail, e.toString())
                snackbarHostState.showSnackbar(e.message!!)

            }
        }
    }

    fun getActivities() {
        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                updateHttpState(message = "获取活动信息")
                val activities = secondClass.getActivities()
                if(activities.success) {
                    updateActivities(activities.value!!)
                    updateHttpState(HttpStatus.Success, activities.message)
                }else throw Exception(activities.message)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                updateHttpState(HttpStatus.Fail, e.toString())
                snackbarHostState.showSnackbar(e.message!!)
            }
        }
    }

    fun init() {
        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                updateHttpState(message = "登录中")
                val res = secondClass.login(account)
                Log.i(TAG, "login secondclass ${res.message}")
                if (res.success) {
                    updateHttpState(message = "获取用户信息")
                    val user = secondClass.getUser()
                    this@PageViewModel.user = user.value!!
                    val scoreInfo = secondClass.getScoreInfo(this@PageViewModel.user)
                    this@PageViewModel.scoreInfo = scoreInfo.value!!
                    this@PageViewModel.getActivities()
                    updateHttpState(HttpStatus.Success, res.message)
                } else {
                    updateHttpState(HttpStatus.Fail, res.message)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                updateHttpState(HttpStatus.Fail, e.toString())
                if(e.message!!.contains("500 Server internal error")) snackbarHostState.showSnackbar("使用前请勿在其他端登录，请等待几分钟后重新登录")
                else snackbarHostState.showSnackbar(e.message!!)
            }
        }
    }

}