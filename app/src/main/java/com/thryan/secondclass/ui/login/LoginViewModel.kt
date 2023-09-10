package com.thryan.secondclass.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.ui.AppDataStore
import com.thryan.secondclass.core.Webvpn
import com.thryan.secondclass.ui.Navigator
import com.thryan.secondclass.ui.info.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appDataStore: AppDataStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState("", "", "", false, "", false, false))
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private var login = false

    init {
        Repository.activities.value = emptyList()
        send(LoginIntent.GetPreference)
    }

    private suspend fun update(uiStates: LoginState) = _uiState.emit(uiStates)

    fun send(intent: LoginIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> {
                update(uiState.value.copy(pending = true))
                Log.i(TAG, "login ${uiState.value.account}:${uiState.value.password}")
                val twfid = appDataStore.getTwfid("")
                val (account, password, scAccount) = uiState.value
                //检查缓存的twfid是否可用
                if (twfid.isNotEmpty() && Webvpn.checkLogin(twfid)) {
                    withContext(Dispatchers.Main) {
                        navigator.navigate("page?twfid=${twfid}&account=${scAccount.ifEmpty { account }}") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    login(account, password, scAccount)
                }
                update(uiState.value.copy(pending = false))
            }

            is LoginIntent.GetPreference -> {
                update(
                    uiState.value.copy(
                        account = appDataStore.getAccount(""),
                        password = appDataStore.getPassword("")
                    )
                )
            }

            is LoginIntent.UpdateAccount -> {
                update(uiState.value.copy(account = intent.account))
            }

            is LoginIntent.UpdatePassword -> {
                update(uiState.value.copy(password = intent.password))
            }

            is LoginIntent.UpdateSCAccount -> {
                update(uiState.value.copy(scAccount = intent.scAccount))
            }

            is LoginIntent.CloseDialog -> {
                update(uiState.value.copy(showDialog = false))
            }

            is LoginIntent.UpdatePasswordVisible -> {
                update(uiState.value.copy(showPassword = intent.visible))
            }
        }
    }


    private suspend fun login(account: String, password: String, scAccount: String = "") {
        val response = Webvpn.login(account, password)
        //登录
        if (response.message == "请求成功") {
            with(appDataStore) {
                putTwfid(response.data)
                Log.i(TAG, account)
                putAccount(account)
                putPassword(password)
            }
            withContext(Dispatchers.Main) {
                navigator.navigate("page?twfid=${response.data}&account=${scAccount.ifEmpty { account }}") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else {
            if (!login) {
                login = true
                login(account, password, scAccount)
            } else {
                val msg = when {
                    response.message.contains("CAPTCHA required") -> "需要验证码，请一段时间后再试"
                    response.message.contains("Invalid username or password") -> "账号或密码错误"
                    response.message.contains("maybe attacked") -> "尝试次数过多，一段时间后再试"
                    else -> response.message
                }
                update(uiState.value.copy(showDialog = true, message = response.message))
            }
        }

    }


    companion object {
        private const val TAG = "LoginViewModel"
    }

}
