package com.thryan.secondclass.ui.page

import com.thryan.secondclass.core.result.SCActivity

data class PageState(
    val loading: Boolean,
    val loadingMsg: String,
    val activities: List<SCActivity>,
    val showingDialog: Boolean,
    val dialogContent: String,
    val loadMore: Boolean,
    val keyword: String
)