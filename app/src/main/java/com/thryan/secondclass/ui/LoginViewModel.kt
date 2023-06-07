package com.thryan.secondclass.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.Webvpn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _uiState = MutableStateFlow(LoginState(false, ""))
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()


    private val _cityName = MutableLiveData<String>()

    /**
     * 对外单独暴漏修改城市名方法
     */
    fun updateUiState(login: Boolean, message: String) {
        _uiState.update { currentState ->
            currentState.copy(logined = login, message = message)

        }
    }

    fun login(account: String, password: String) {
        Log.i(TAG,account)
        Log.i(TAG,password)
        this.viewModelScope.launch(Dispatchers.IO) {
            val res = Webvpn().login(account, password)
            if(res.message.contains("Invalid username or password"))
            updateUiState(true, "学号或密码错误")
            else
                updateUiState(true, res.message)
        }
    }

}
