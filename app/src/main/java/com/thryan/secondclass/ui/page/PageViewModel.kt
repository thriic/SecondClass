package com.thryan.secondclass.ui.page

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.result.UserInfo
import com.thryan.secondclass.Navigator
import com.thryan.secondclass.SCRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val navigator: Navigator,
    private val scRepository: SCRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _pageState = MutableStateFlow(
        PageState(
            loading = false,
            loadingMsg = "",
            showingDialog = false,
            dialogContent = "",
            activities = emptyList(),
            loadMore = true,
            keyword = ""
        )
    )
    val pageState: StateFlow<PageState> = _pageState.asStateFlow()


    private lateinit var userInfo: UserInfo
    private val twfid = savedStateHandle.get<String>("twfid") ?: throw Exception()
    private val account = savedStateHandle.get<String>("account") ?: throw Exception()
    private val password = savedStateHandle.get<String>("password")
    private var currentPageNum = 1

    private var userJob: Job? = null

    init {
        Log.i(TAG, "PageViewModel Created")
        scRepository.init(twfid, account, password)
        send(PageIntent.Init)
        viewModelScope.launch {
            scRepository.activities.collect {
                update {
                    copy(activities = it)
                }
            }
        }
    }


    private suspend fun update(pageState: PageState) = _pageState.emit(pageState)
    fun send(intent: PageIntent) = viewModelScope.launch { onHandle(intent) }
    private suspend fun onHandle(intent: PageIntent) {
        when (intent) {
            is PageIntent.Init -> {
                login()
            }

            is PageIntent.UpdateActivity -> {
            }

            is PageIntent.OpenActivity -> {
                navigator.navigate("info?id=${intent.id}")
            }

            is PageIntent.ShowDialog -> {
                val content = if (intent.userInfo) {
                    userJob?.join()
                    buildString {
                        append(userInfo.name)
                        append("\n积分:")
                        append(userInfo.score)
                        append(" 完成活动:")
                        append(userInfo.activity)
                        append(" 诚信值:")
                        append(userInfo.integrity_value)
                    }
                } else intent.message
                update(PageActions.Dialog(content).reduce(pageState.value))
            }

            is PageIntent.CloseDialog -> {
                update(pageState.value.copy(showingDialog = false))
            }

            PageIntent.LoadMore -> {
                getActivities()
            }

            is PageIntent.Search -> {
                if (intent.keyword != pageState.value.keyword) {
                    //清空列表
                    update {
                        copy(keyword = intent.keyword, loadMore = true)
                    }
                    currentPageNum = 1
                    getActivities(clear = true)
                }
            }
        }
    }

    override fun onCleared() {
        Log.i(TAG, "clear pageViewModel")
    }


    private suspend fun getActivities(pageSize: Int = 5, clear: Boolean = false) {
        try {
            val size =
                scRepository.getActivities(currentPageNum, pageSize, pageState.value.keyword, clear)
            if (size <= 0) update { copy(loadMore = false) }
            currentPageNum += 1
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            update(PageActions.Dialog(e.message!!).reduce(pageState.value))
        }
    }


    private suspend fun login() = withContext(Dispatchers.IO) {
        try {
            update(PageActions.Loading("登录中").reduce(pageState.value))
            val loginResult = scRepository.login()
            Log.i(TAG, "login secondclass $loginResult")
            update(PageActions.Loading("获取活动信息").reduce(pageState.value))
            val activityJob = launch { this@PageViewModel.getActivities() }
            userJob = launch { this@PageViewModel.userInfo = scRepository.getUserInfo() }
            activityJob.join()
            update(PageActions.Loading(loading = false).reduce(pageState.value))
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            if (e.message?.contains("500 Server internal error") == true) update(
                PageActions.Dialog("使用前请勿在其他端登录，请等待几分钟后重新登录")
                    .reduce(pageState.value)
            )
            else update(PageActions.Dialog(e.message ?: "error").reduce(pageState.value))
        }

    }

    private suspend fun update(block: PageState.() -> PageState) {
        val newState: PageState
        pageState.value.apply { newState = block() }
        _pageState.emit(newState)
    }

    companion object {
        private const val TAG = "PageViewModel"
    }

}