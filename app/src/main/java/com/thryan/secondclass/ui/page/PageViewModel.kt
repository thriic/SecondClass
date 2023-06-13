package com.thryan.secondclass.ui.page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.thryan.secondclass.R
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.signIn
import com.thryan.secondclass.core.utils.success
import com.thryan.secondclass.ui.info.Repository
import com.thryan.secondclass.ui.login.HttpStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PageViewModel(
    val navController: NavHostController,
    private val twfid: String,
    private val account: String
) :
    ViewModel() {
    private val TAG = "PageViewModel"

    private val _pageState = MutableStateFlow(
        PageState(
            loading = false,
            loadingMsg = "",
            showingDialog = false,
            dialogContent = "",
            activities = Repository.activities.value
        )
    )
    val pageState: StateFlow<PageState> = _pageState.asStateFlow()


    private lateinit var user: User
    private lateinit var scoreInfo: ScoreInfo

    init {
        Log.i(TAG,"PageViewModel Created")
        Repository.secondClass = SecondClass(twfid)
    }


    private suspend fun update(pageState: PageState) = _pageState.emit(pageState)
    fun send(intent: PageIntent) = viewModelScope.launch { onHandle(intent) }
    private suspend fun onHandle(intent: PageIntent) {
        when (intent) {
            is PageIntent.Init -> {
                if(Repository.activities.value.isEmpty()) login()
            }
            is PageIntent.OpenActivity -> {
                navController.navigate("info?id=${intent.id}")
            }

            is PageIntent.ShowDialog -> {
                val content = if (intent.type == 1) {
                    buildString {
                        append(user.name)
                        append("\n积分:")
                        append(scoreInfo.score)
                        append(" 完成活动:")
                        append(scoreInfo.activity)
                        append(" 诚信值:")
                        append(scoreInfo.integrity_value)
                    }
                } else {
                    "此软件提供在任意时间对已报名活动进行签到的功能，且后台记录数据为活动进行中的时间。\\n理论上风险较小，但需要注意二课的审核机制，鉴别该活动是否与自己相关再进行报名签到\\n本项目仅供开发学习使用"
                }
                update(PageActions.Dialog(content).reduce(pageState.value))
            }
            is PageIntent.CloseDialog -> {
                update(pageState.value.copy(showingDialog = false))
            }
        }
    }



    private suspend fun getActivities() {
        try {
            update(PageActions.Loading("获取活动信息").reduce(pageState.value))
            val activities = Repository.secondClass.getActivities()
            if (activities.success()) {
                update(pageState.value.copy(activities = activities.data.rows))
                Repository.activities.emit(activities.data.rows)
            } else throw Exception(activities.message)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            update(PageActions.Dialog(e.message!!).reduce(pageState.value))
        }
    }


    suspend fun login() {
        try {
            update(PageActions.Loading("登录中").reduce(pageState.value))
            val res = Repository.secondClass.login(account)
            Log.i(TAG, "login secondclass ${res.message}")
            if (res.success()) {
                update(PageActions.Loading("获取用户信息").reduce(pageState.value))
                val user = Repository.secondClass.getUser()
                this@PageViewModel.user = user.data
                val scoreInfo = Repository.secondClass.getScoreInfo(this@PageViewModel.user)
                this@PageViewModel.scoreInfo = scoreInfo.data
                this@PageViewModel.getActivities()
                update(PageActions.Loading(loading = false).reduce(pageState.value))
            } else {
                update(PageActions.Dialog(res.message).reduce(pageState.value))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            if (e.message!!.contains("500 Server internal error")) PageActions.Dialog(
                "使用前请勿在其他端登录，请等待几分钟后重新登录"
            ).reduce(pageState.value)
            else update(PageActions.Dialog(e.message!!).reduce(pageState.value))
        }

    }

}