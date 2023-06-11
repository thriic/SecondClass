package com.thryan.secondclass.ui.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.thryan.secondclass.core.Webvpn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
class LoginViewModel(private val context: Context, private val navController: NavHostController) :
    ViewModel() {

    private val TAG = "LoginViewModel"

    val sharedPref = context.getSharedPreferences(
        "com.thryan.secondclass.PREFERENCE", Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(LoginState(false, ""))
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()


    suspend fun init() {
        //sharedPref.getString("twfid", "")?.let { checkLogin(it) }
    }

    /**
     * 对外单独暴漏修改城市名方法
     */
    fun updateUiState(login: Boolean, message: String = "") {
        _uiState.update { currentState ->
            currentState.copy(
                fail = login,
                message = message.substringAfter("<![CDATA[").substringBefore("]]>")
            )
        }
    }


    fun login(account: String, password: String,scAccount:String = account) {
        this.viewModelScope.launch(Dispatchers.IO) {
            sharedPref.getString("twfid", "")?.let {
                if (Webvpn.checkLogin(it)) withContext(Dispatchers.Main) {
                    navController.navigate("page?twfid=$it&account=${scAccount.ifEmpty { account }}")
                    this@launch
                }
                else {
                    val res = Webvpn().login(account, password)
                    if (res.message == "请求成功") {
                        with(sharedPref.edit()) {
                            putString("twfid", res.data)
                            putString("account", account)
                            putString("password", password)
                            apply()
                        }
                        withContext(Dispatchers.Main) {
                            navController.navigate("page?twfid=${res.data}&account=${scAccount.ifEmpty { account }}"){
                                launchSingleTop = true
                            }
                        }
                    } else updateUiState(true, res.message!!)
                }
            }

        }
    }

}
