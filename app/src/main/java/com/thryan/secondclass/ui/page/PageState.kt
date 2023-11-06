package com.thryan.secondclass.ui.page

import cn.thriic.common.data.SCActivity

data class PageState(
    val loading: Boolean,
    val loadingMsg: String,
    val activities: List<SCActivity>,
    val showingDialog: Boolean,
    val dialogContent: String,
    val loadMore: Boolean
)

data class FilterState(
    val keyword:String,
    val onlySign:Boolean,
    val status: String,
    val type:String
)