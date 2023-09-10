package com.thryan.secondclass.ui.page

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.User
import com.thryan.secondclass.core.utils.success
import com.thryan.secondclass.ui.Navigator
import com.thryan.secondclass.ui.info.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _pageState = MutableStateFlow(
        PageState(
            loading = false,
            loadingMsg = "",
            showingDialog = false,
            dialogContent = "",
            activities = Repository.activities.value,
            loadMore = true,
            keyword = ""
        )
    )
    val pageState: StateFlow<PageState> = _pageState.asStateFlow()


    private lateinit var user: User
    private lateinit var scoreInfo: ScoreInfo
    private val twfid = savedStateHandle.get<String>("twfid") ?: throw Exception()
    private val account = savedStateHandle.get<String>("account") ?: throw Exception()
    private var secondClass = SecondClass(twfid)
    private var currentPageNum = 1

    init {
        Log.i(TAG, "PageViewModel Created")
        send(PageIntent.Init)
    }


    private suspend fun update(pageState: PageState) = _pageState.emit(pageState)
    fun send(intent: PageIntent) = viewModelScope.launch { onHandle(intent) }
    private suspend fun onHandle(intent: PageIntent) {
        when (intent) {
            is PageIntent.Init -> {
                if (Repository.activities.value.isEmpty()) login()
            }

            is PageIntent.UpdateActivity -> {
                update(pageState.value.copy(activities = Repository.activities.value))
            }

            is PageIntent.OpenActivity -> {
                Repository.activities.emit(pageState.value.activities)
                navigator.navigate("info?id=${intent.id}&twfid=${twfid}&token=${secondClass.token}")
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

            PageIntent.LoadMore -> {
                val res = getActivities(currentPageNum)
                if (res) currentPageNum += 1
                else update(pageState.value.copy(loadMore = false))
            }

            is PageIntent.Search -> {
                if (intent.keyword != pageState.value.keyword) {
                    update(
                        pageState.value.copy(
                            keyword = intent.keyword,
                            activities = emptyList()
                        )
                    )
                    currentPageNum = 1
                    getActivities()
                }
            }
        }
    }

    override fun onCleared() {
        Log.i(TAG, "clear pageViewModel")
    }


    private suspend fun getActivities(pageNo: Int = 1, pageSize: Int = 5): Boolean =
        try {
            val activities = secondClass.getActivities(pageNo, pageSize, pageState.value.keyword)
            if (activities.success()) {
                if (activities.data.rows.isEmpty()) {
                    false
                } else {
                    update(PageActions.LoadMore(activities.data.rows).reduce(_pageState.value))
                    true
                }
            } else throw Exception(activities.message)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            update(PageActions.Dialog(e.message!!).reduce(pageState.value))
            false
        }


    private suspend fun login() {
        try {
            update(PageActions.Loading("登录中").reduce(pageState.value))
            val res = secondClass.login(account)
            Log.i(TAG, "login secondclass ${res.message}")
            if (res.success()) {
                Repository.secondClass = secondClass
                update(PageActions.Loading("获取用户信息").reduce(pageState.value))
                val user = secondClass.getUser()
                this@PageViewModel.user = user.data
                val scoreInfo = secondClass.getScoreInfo(this@PageViewModel.user)
                this@PageViewModel.scoreInfo = scoreInfo.data
                //获取活动
                update(PageActions.Loading("获取活动信息").reduce(pageState.value))
                this@PageViewModel.getActivities()
                currentPageNum = 3
                update(PageActions.Loading(loading = false).reduce(pageState.value))
            } else {
                update(PageActions.Dialog(res.message).reduce(pageState.value))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
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