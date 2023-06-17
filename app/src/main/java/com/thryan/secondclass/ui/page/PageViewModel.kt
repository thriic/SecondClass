package com.thryan.secondclass.ui.page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.success
import com.thryan.secondclass.ui.info.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PageViewModel(
    private val navController: NavHostController,
    val twfid: String,
    val account: String
) :
    ViewModel() {

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
    private var secondClass = SecondClass(twfid)

    init {
        Log.i(Companion.TAG, "PageViewModel Created")
    }


    private suspend fun update(pageState: PageState) = _pageState.emit(pageState)
    fun send(intent: PageIntent) = viewModelScope.launch { onHandle(intent) }
    private suspend fun onHandle(intent: PageIntent) {
        when (intent) {
            is PageIntent.Init -> {
                if (Repository.activities.value.isEmpty()) login()
            }

            is PageIntent.UpdateActivity ->{
                update(pageState.value.copy(activities = Repository.activities.value))
            }

            is PageIntent.OpenActivity -> {
                Log.i(Companion.TAG, "跳转界面")
                navController.navigate("info?id=${intent.id}&twfid=${twfid}&token=${secondClass.token}")
                //_oneTimeEvent.emit(UiEvent.Navigate(id = intent.id, twfid = twfid, token = secondClass.token))
            }

            is PageIntent.ShowDialog -> {
                val content = if (intent.userInfo) {
                    buildString {
                        append(user.name)
                        append("\n积分:")
                        append(scoreInfo.score)
                        append(" 完成活动:")
                        append(scoreInfo.activity)
                        append(" 诚信值:")
                        append(scoreInfo.integrity_value)
                    }
                } else intent.message
                update(PageActions.Dialog(content).reduce(pageState.value))
            }

            is PageIntent.CloseDialog -> {
                update(pageState.value.copy(showingDialog = false))
            }
        }
    }

    override fun onCleared() {
        //Repository.activities.update { emptyList() }
        Log.i(Companion.TAG, "clear pageViewModel")
    }


    private suspend fun getActivities() {
        try {
            update(PageActions.Loading("获取活动信息").reduce(pageState.value))
            val activities = secondClass.getActivities()
            if (activities.success()) {
                update(pageState.value.copy(activities = activities.data.rows))
                Repository.activities.emit(activities.data.rows)
            } else throw Exception(activities.message)
        } catch (e: Exception) {
            Log.e(Companion.TAG, e.toString())
            update(PageActions.Dialog(e.message!!).reduce(pageState.value))
        }
    }


    private suspend fun login() {
        try {
            update(PageActions.Loading("登录中").reduce(pageState.value))
            val res = secondClass.login(account)
            Log.i(Companion.TAG, "login secondclass ${res.message}")
            if (res.success()) {
                Repository.secondClass = secondClass
                update(PageActions.Loading("获取用户信息").reduce(pageState.value))
                val user = secondClass.getUser()
                this@PageViewModel.user = user.data
                val scoreInfo = secondClass.getScoreInfo(this@PageViewModel.user)
                this@PageViewModel.scoreInfo = scoreInfo.data
                this@PageViewModel.getActivities()
                update(PageActions.Loading(loading = false).reduce(pageState.value))
            } else {
                update(PageActions.Dialog(res.message).reduce(pageState.value))
            }
        } catch (e: Exception) {
            Log.e(Companion.TAG, e.toString())
            if (e.message!!.contains("500 Server internal error")) update(
                PageActions.Dialog(
                    "使用前请勿在其他端登录，请等待几分钟后重新登录"
                ).reduce(pageState.value)
            )
            else update(PageActions.Dialog(e.message!!).reduce(pageState.value))
        }

    }

    companion object {
        private const val TAG = "PageViewModel"
    }

}