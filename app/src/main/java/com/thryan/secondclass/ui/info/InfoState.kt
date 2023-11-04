package com.thryan.secondclass.ui.info

import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo
import java.time.LocalDateTime

data class InfoState(
    val activity: SCActivity,
    val signInfo: SignInfo,
    val signInTime: LocalDateTime,
    val signOutTime: LocalDateTime,
    val loading: Boolean,
    val showDialog: Dialog?,
    val link: String
)

enum class Dialog {
    SignInTime, SignInDate, SignOutTime, SignOutDate
}