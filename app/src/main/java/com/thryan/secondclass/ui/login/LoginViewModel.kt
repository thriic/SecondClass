package com.thryan.secondclass.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.thriic.common.WebVpn
import cn.thriic.common.data.VpnInfo
import com.thryan.secondclass.AppDataStore
import com.thryan.secondclass.Navigator
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
class LoginViewModel @Inject constructor(
    private val appDataStore: AppDataStore,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState("", "", "", false, "", false, false))
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    var auth: VpnInfo? = null
    var job: Job
    var checkJob: Job? = null
    private var login = false

    init {
        send(LoginIntent.Init)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                //若超过10分钟则不检查twfid可用性
                val lastTime = appDataStore.getLastTime("")
                if (lastTime.isNotEmpty() && System.currentTimeMillis() - lastTime.toLong() < 60 * 60 * 1000) {
                    Log.i(TAG, "距离上次登录时间小于1小时，检查twfid可用性")
                    val twfid = appDataStore.getTwfid("")
                    if (twfid.isNotEmpty()) {
                        Log.i(TAG, "检查twfid")
                        login = WebVpn.checkLogin(twfid)
                        if (!login) checkJob = launch { auth = WebVpn.auth().data }
                        else Log.i(TAG, "twfid有效")
                        return@launch
                    }
                }
                Log.i(TAG, "twfid无效，预加载auth")
                auth = WebVpn.auth().data
                Log.i(TAG, "auth加载完毕")
            } catch (e: Exception) {
                update(
                    uiState.value.copy(
                        showDialog = true,
                        message = if (e.message?.contains("time") == true) "连接超时" else e.message
                            ?: "未知错误，请等待一段时间后尝试"
                    )
                )
            }
        }
    }

    private suspend fun update(uiStates: LoginState) = _uiState.emit(uiStates)

    fun send(intent: LoginIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> {
                try {
                    if (uiState.value.account.isEmpty() || uiState.value.password.isEmpty()) {
                        update(
                            uiState.value.copy(
                                showDialog = true,
                                message = "请输入账号密码"
                            )
                        )
                        return
                    }
                    update(uiState.value.copy(pending = true))
                    Log.i(TAG, "login ${uiState.value.account}:${uiState.value.password}")
                    val twfid = appDataStore.getTwfid("")
                    val (account, password, scPassword) = uiState.value
                    job.join()
                    Log.i(TAG, "complete login  $login")
                    if (login) {
                        appDataStore.putLastTime(
                            System.currentTimeMillis().toString()
                        )
                        withContext(Dispatchers.Main) {
                            navigator.navigate("page?twfid=${twfid}&account=${account}&password=$scPassword") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    } else {
                        Log.i(TAG, "尝试登录")
                        login(account, password, scPassword = scPassword)
                    }
                } catch (e: Exception) {
                    Log.i(TAG, "error $e")
                    update(
                        uiState.value.copy(
                            showDialog = true,
                            message = if (e.message?.contains("time") == true) "连接超时" else e.message
                                ?: "未知错误，请等待一段时间后尝试"
                        )
                    )
                } finally {
                    update(uiState.value.copy(pending = false))
                }
            }

            is LoginIntent.GetPreference -> {

            }

            is LoginIntent.UpdateAccount -> {
                update(uiState.value.copy(account = intent.account))
            }

            is LoginIntent.UpdatePassword -> {
                update(uiState.value.copy(password = intent.password))
            }

            is LoginIntent.UpdateSCAccount -> {
                update(uiState.value.copy(scPassword = intent.scAccount))
            }

            is LoginIntent.CloseDialog -> {
                update(uiState.value.copy(showDialog = false))
            }

            is LoginIntent.UpdatePasswordVisible -> {
                update(uiState.value.copy(showPassword = intent.visible))
            }

            is LoginIntent.Init -> {
                update(
                    uiState.value.copy(
                        account = appDataStore.getAccount(""),
                        password = appDataStore.getPassword(""),
                        scPassword = appDataStore.getScPassword("")
                    )
                )

            }
        }
    }


    private suspend fun login(
        account: String,
        password: String,
        scAccount: String = "",
        scPassword: String = "123456"
    ) =
        withContext(Dispatchers.IO) {
            checkJob?.join()
            val response = WebVpn.login(auth!!, account, password)
            //登录
            if (response.message == "请求成功") {
                Log.i(TAG, "appDataStore")
                with(appDataStore) {
                    putTwfid(response.data)
                    putAccount(account)
                    putPassword(password)
                    if (scPassword != "123456") putScPassword(scPassword)
                    putLastTime(System.currentTimeMillis().toString())
                }
                Log.i(TAG, "appDataStore complete")
                withContext(Dispatchers.Main) {
                    navigator.navigate("page?twfid=${response.data}&account=${scAccount.ifEmpty { account }}&password=$scPassword") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                checkJob = launch {
                    auth = WebVpn.auth().data
                }
                update(uiState.value.copy(showDialog = true, message = response.message))
            }

        }


    companion object {
        private const val TAG = "LoginViewModel"
    }

}
