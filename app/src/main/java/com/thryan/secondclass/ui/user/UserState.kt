package com.thryan.secondclass.ui.user

import com.thryan.secondclass.core.result.ScoreInfo
import com.thryan.secondclass.core.result.User

data class UserState(
    val user: User,
    val scoreInfo: ScoreInfo,
    val radarScore: List<RadarScore>,
    val loading: Boolean,
    val dynamic: Boolean
)