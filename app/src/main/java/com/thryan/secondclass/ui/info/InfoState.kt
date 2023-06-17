package com.thryan.secondclass.ui.info

import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo

data class InfoState(
    val activity: SCActivity,
    val signInfo: SignInfo,
    val signInTime: String,
    val signOutTime: String,
    val showSignCard: Boolean,
    val showSignInCard: Boolean,
    val loading: Boolean,
    val showSignOutTimePicker: Boolean,
    val showSignInTimePicker: Boolean,
    val link: String
)