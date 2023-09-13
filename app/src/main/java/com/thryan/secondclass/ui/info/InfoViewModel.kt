package com.thryan.secondclass.ui.info

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import com.thryan.secondclass.core.utils.toLocalDateTime
import com.thryan.secondclass.SCRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val scRepository: SCRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val id = savedStateHandle.get<String>("id")!!

    private val _uiState =
        MutableStateFlow(
            InfoState(
                scRepository.getActivity(id)!!,
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


    init {
        viewModelScope.launch {
            try {
                //生成链接
                generateLink()
                //获取签到信息
                val signInfo = scRepository.getSignInfo(uiState.value.activity)
                    .getOrElse(0) { SignInfo(id, "", "") }
                update {
                    copy(
                        signInfo = signInfo,
                        signOutTime = signInfo.signOutTime.substringBefore(".000").ifEmpty {
                            uiState.value.activity.endTime.before(
                                (5..15).random()
                            )
                        },
                        signInTime = signInfo.signInTime.substringBefore(".000").ifEmpty {
                            uiState.value.activity.startTime.after(
                                (5..15).random()
                            )
                        },
                        loading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                update { copy(loading = false) }
                showSnackbar(e.message ?: e.toString())
            }
        }
    }

    private suspend fun update(block: InfoState.() -> InfoState) {
        val newState: InfoState
        uiState.value.apply { newState = block() }
        _uiState.emit(newState)
    }

    fun send(intent: InfoIntent) = viewModelScope.launch { onHandle(intent) }

    private suspend fun onHandle(infoIntent: InfoIntent) {
        Log.i(TAG, infoIntent.toString())
        when (infoIntent) {
            is InfoIntent.UpdateSignInTime -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime =
                    LocalDateTime.parse(uiState.value.signInTime, formatter).toLocalDate()
                val localDateTime = infoIntent.signInTime.atDate(dateTime)
                if (localDateTime.isBefore(uiState.value.signOutTime.toLocalDateTime())) {
                    val timeString = localDateTime.format(formatter)
                    Log.i(TAG, timeString)
                    update { copy(signInTime = timeString) }
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
                    Log.i(TAG, timeString)
                    update { copy(signOutTime = timeString) }
                } else {
                    showSnackbar("签退时间不得早于签到时间")
                }
            }

            is InfoIntent.ShowDialog -> {
                if (infoIntent.signInDialog) update { copy(showSignInTimePicker = true) }
                else update { copy(showSignOutTimePicker = true) }
            }

            is InfoIntent.CloseDialog -> {
                update {
                    copy(
                        showSignOutTimePicker = false,
                        showSignInTimePicker = false
                    )
                }
            }

            InfoIntent.Sign -> {
                sign()
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
            update { copy(loading = true) }
            val res = scRepository.signIn(activity, uiState.value.signInfo, signInTime, signOutTime)
            //签到成功更新signInfo
            update {
                copy(
                    loading = false,
                    signInfo = SignInfo(uiState.value.signInfo.id, signInTime, signOutTime)
                )
            }
            showSnackbar("签到成功")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            update { copy(loading = false) }
            showSnackbar(e.message ?: e.toString())
        }
    }

    private suspend fun sign() {
        try {
            update { copy(loading = true) }
            val res = scRepository.sign(uiState.value.activity)
            val signInfo = scRepository.getSignInfo(uiState.value.activity)
                .getOrElse(0) { uiState.value.signInfo }
            update {
                copy(
                    loading = false,
                    activity = uiState.value.activity.copy(isSign = "1"),
                    signInfo = signInfo
                )
            }
            scRepository.setActivity(id, "1")
            showSnackbar(res.data.msg)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            update { copy(loading = false) }
            showSnackbar(e.message ?: e.toString())
        }

    }

    private suspend fun generateLink() {
        update {
            copy(
                link = "/#/pages/activity/studentQdqt?id=" + uiState.value.activity.id + "&timestamp=" + System.currentTimeMillis()
                    .plus(500000L)
            )
        }
    }


    companion object {
        private const val TAG = "InfoViewModel"
    }
}