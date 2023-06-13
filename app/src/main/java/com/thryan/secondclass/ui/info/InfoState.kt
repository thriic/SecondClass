package com.thryan.secondclass.ui.info

import com.thryan.secondclass.core.result.SCActivity
import com.thryan.secondclass.core.result.SignInfo

data class InfoState(
    val activity: SCActivity,
    val showSignCard: Boolean,
    val showSignInCard: Boolean,
    val signInfo: SignInfo
)