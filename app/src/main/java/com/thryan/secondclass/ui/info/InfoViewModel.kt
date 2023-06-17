package com.thryan.secondclass.ui.info

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import com.thryan.secondclass.core.utils.success
import com.thryan.secondclass.core.utils.toLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InfoViewModel(val id: String, twfid: String, token: String) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            InfoState(
                Repository.getActivity(id)!!,
                loading = true,
                showSignCard = true,
                showSignInCard = true,
                signInfo = SignInfo(id, "", ""),
                showSignOutTimePicker = false,
                showSignInTimePicker = false,
                signInTime = "",
                signOutTime = "",
                link = ""
            )
        )
    val uiState: StateFlow<InfoState> = _uiState.asStateFlow()

    val snackbarState = SnackbarHostState()

    private val secondClass = SecondClass(twfid, token)

    init {

        viewModelScope.launch {
            try {
                //生成链接
                generateLink()
                //获取签到信息
                val res = secondClass.getSignInfo(Repository.getActivity(id)!!)
                if (!res.success()) throw Exception(res.message)
                val signInfo = res.data.rows.getOrElse(0) { SignInfo(id, "", "") }
                update(
                    _uiState.value.copy(
                        signInfo = signInfo,
                        signOutTime = signInfo.signOutTime.ifEmpty {
                            uiState.value.activity.endTime.before(
                                10
                            )
                        },
                        signInTime = signInfo.signInTime.ifEmpty {
                            uiState.value.activity.startTime.after(
                                10
                            )
                        },
                        loading = false
                    )
                )
            } catch (e: Exception) {
                Log.e(Companion.TAG, e.toString())
                update(uiState.value.copy(loading = false))
                showSnackbar(e.message ?: e.toString())
            }
        }
    }

    private suspend fun update(infoState: InfoState) = _uiState.emit(infoState)

    fun send(intent: InfoIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(infoIntent: InfoIntent) {
        Log.i(Companion.TAG, infoIntent.toString())
        when (infoIntent) {
            is InfoIntent.UpdateSignInTime -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime =
                    LocalDateTime.parse(uiState.value.signInTime, formatter).toLocalDate()
                val localDateTime = infoIntent.signInTime.atDate(dateTime)
                if (localDateTime.isBefore(uiState.value.signOutTime.toLocalDateTime())) {
                    val timeString = localDateTime.format(formatter)
                    Log.i(Companion.TAG, timeString)
                    update(uiState.value.copy(signInTime = timeString))
                } else {
                    showSnackbar("签到时间不得晚于签退时间")
                }
            }

            is InfoIntent.UpdateSignOutTime -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime =
                    LocalDateTime.parse(uiState.value.signOutTime, formatter).toLocalDate()
                val localDateTime = infoIntent.signOutTime.atDate(dateTime)
                if (localDateTime.isAfter(uiState.value.signInTime.toLocalDateTime())) {
                    val timeString = localDateTime.format(formatter)
                    Log.i(Companion.TAG, timeString)
                    update(uiState.value.copy(signOutTime = timeString))
                } else {
                    showSnackbar("签退时间不得早于签到时间")
                }
            }

            is InfoIntent.ShowDialog -> {
                if (infoIntent.signInDialog) update(uiState.value.copy(showSignInTimePicker = true))
                else update(uiState.value.copy(showSignOutTimePicker = true))
            }

            is InfoIntent.CloseDialog -> {
                update(
                    uiState.value.copy(
                        showSignOutTimePicker = false,
                        showSignInTimePicker = false
                    )
                )
            }

            InfoIntent.Sign -> {
                sign(uiState.value.activity)
            }

            InfoIntent.SignIn -> {
                val (activity, _, signInTime, signOutTime) = uiState.value
                signIn(activity, signInTime, signOutTime)
            }

            InfoIntent.GenerateLink -> generateLink()
        }
    }

    private suspend fun showSnackbar(message: String) {
        snackbarState.showSnackbar(
            message = message,
            actionLabel = "OK",
            duration = SnackbarDuration.Short
        )
    }

    private suspend fun signIn(activity: SCActivity, signInTime: String, signOutTime: String) {
        try {
            update(uiState.value.copy(loading = true))
            val res = secondClass.signIn(activity, uiState.value.signInfo, signInTime, signOutTime)
            if (res.success()) {
                //签到成功更新signInfo
                update(
                    uiState.value.copy(
                        loading = false,
                        signInfo = SignInfo(uiState.value.signInfo.id, signInTime, signOutTime)
                    )
                )
                showSnackbar("签到成功")
            } else {
                throw Exception(res.message)
            }
        } catch (e: Exception) {
            Log.e(Companion.TAG, e.toString())
            update(uiState.value.copy(loading = false))
            showSnackbar(e.message ?: e.toString())
        }
    }

    private suspend fun sign(activity: SCActivity) {
        try {
            update(uiState.value.copy(loading = true))
            val res = secondClass.sign(activity)
            if (!res.success()) throw Exception(res.message)
            if (res.data.code == "1") {
                update(
                    uiState.value.copy(
                        loading = false,
                        activity = uiState.value.activity.copy(isSign = "1"),
                        signInfo = secondClass.getSignInfo(activity).data.rows.getOrElse(0) { uiState.value.signInfo }
                    )
                )
                Repository.setActivity(activity.id, "1")
                showSnackbar(res.data.msg)
            } else {
                update(uiState.value.copy(loading = false))
                showSnackbar(res.data.msg)
            }
        } catch (e: Exception) {
            Log.e(Companion.TAG, e.toString())
            update(uiState.value.copy(loading = false))
            showSnackbar(e.message ?: e.toString())
        }

    }

    private suspend fun generateLink() {
        update(
            uiState.value.copy(
                link = "/#/pages/activity/studentQdqt?id=" + uiState.value.activity.id + "&timestamp=" + System.currentTimeMillis()
                    .plus(500000L)
            )
        )
    }

    companion object {
        private const val TAG = "InfoViewModel"
    }
}