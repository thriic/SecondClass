package com.thryan.secondclass.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thryan.secondclass.ui.AppDataStore
import com.thryan.secondclass.core.WebVpn
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.result.VpnInfo
import com.thryan.secondclass.ui.Navigator
import com.thryan.secondclass.ui.info.Repository
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
        Repository.activities.value = emptyList()
        send(LoginIntent.Init)
        job = viewModelScope.launch(Dispatchers.IO) {
            //若超过10分钟则不检查twfid可用性
            val lastTime = appDataStore.getLastTime("").also(::println)
            if (lastTime.isNotEmpty() && System.currentTimeMillis() - lastTime.toLong() < 10 * 60 * 1000) {
                Log.i(TAG, "距离上次登录时间小于10分钟，检查twfid可用性")
                val twfid = appDataStore.getTwfid("")
                if (twfid.isNotEmpty()) {
                    Log.i(TAG, "检查twfid")
                    login = WebVpn.checkLogin(twfid)
                    Log.i(TAG, "twfid有效")
                    return@launch
                }
            }
            Log.i(TAG, "twfid无效，预加载auth")
            auth = WebVpn.auth().data
            Log.i(TAG, "auth加载完毕")
        }
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
                job.join()
                if (login) {
                    appDataStore.putLastTime(System.currentTimeMillis().toString().also(::println))
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

            is LoginIntent.Init -> {
                update(
                    uiState.value.copy(
                        account = appDataStore.getAccount(""),
                        password = appDataStore.getPassword("")
                    )
                )

            }
        }
    }


    private suspend fun login(account: String, password: String, scAccount: String = "") =
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
                    putLastTime(System.currentTimeMillis().toString())
                }
                Log.i(TAG, "appDataStore complete")
                withContext(Dispatchers.Main) {
                    navigator.navigate("page?twfid=${response.data}&account=${scAccount.ifEmpty { account }}") {
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
