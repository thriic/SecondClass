package com.thryan.secondclass.ui.info

import cn.thriic.common.data.SCActivity
import cn.thriic.common.data.SignInfo
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