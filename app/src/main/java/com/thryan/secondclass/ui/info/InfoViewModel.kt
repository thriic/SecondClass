package com.thryan.secondclass.ui.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.utils.signIn
import com.thryan.secondclass.core.utils.success
import com.thryan.secondclass.ui.login.HttpStatus
import com.thryan.secondclass.ui.login.LoginState
import com.thryan.secondclass.ui.page.PageIntent
import com.thryan.secondclass.ui.page.PageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel(id: String) : ViewModel() {
    private val _uiState =
        MutableStateFlow(InfoState(Repository.getActivity(id)!!, true, true, signInfo = SignInfo(id,"","")))
    val uiState: StateFlow<InfoState> = _uiState.asStateFlow()

    init {

    }

    private suspend fun update(infoState: InfoState) = _uiState.emit(infoState)

    fun send(intent: InfoIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(infoIntent: InfoIntent){

    }
//    fun signIn(activity: SCActivity) {
//        this.viewModelScope.launch(Dispatchers.IO) {
//            try {
//                updateLoadingMsg("签到中")
//                val signInfo = secondClass.getSignInfo(activity)
//                if (!signInfo.success()) throw Exception(signInfo.message)
//                if (signInfo.data.rows[0].signIn()) throw Exception("勿重复签到")
//                val res = secondClass.signIn(activity, signInfo.data.rows[0])
//                if (res.success()) {
//                    updateHttpState(HttpStatus.Success, res.message)
//                    snackbarHostState.showSnackbar("签到成功")
//                } else {
//                    throw Exception(res.message)
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, e.toString())
//                updateHttpState(HttpStatus.Fail, e.toString())
//                snackbarHostState.showSnackbar(e.message!!)
//
//            }
//        }
//    }
//
//    fun sign(activity: SCActivity) {
//        this.viewModelScope.launch(Dispatchers.IO) {
//            try {
//                updateHttpState(HttpStatus.Pending, "报名中")
//                val res = secondClass.sign(activity)
//                if (!res.success()) throw Exception(res.message)
//                if (res.data.code == "1") {
//                    updateHttpState(HttpStatus.Success, res.data.msg)
//                } else
//                    throw Exception(res.data.msg)
//            } catch (e: Exception) {
//                Log.e(TAG, e.toString())
//                updateHttpState(HttpStatus.Fail, e.toString())
//                snackbarHostState.showSnackbar(e.message!!)
//            }
//        }
//    }
}