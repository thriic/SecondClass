package com.thryan.secondclass.ui.user

import cn.thriic.common.data.ScoreInfo
import cn.thriic.common.data.User

data class UserState(
    val user: User,
    val scoreInfo: ScoreInfo,
    val radarScore: List<RadarScore>,
    val loading: Boolean,
    val dynamic: Boolean,
    val webView: Boolean,
    val resign: Boolean
)