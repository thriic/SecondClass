package com.thryan.secondclass.ui.page

import cn.thriic.common.data.SCActivity

sealed class PageActions {
    fun reduce(oldState: PageState): PageState {
        return when (this) {
            is Loading -> if (this.msg != "加载中") oldState.copy(
                loading = this.loading,
                loadingMsg = this.msg
            ) else oldState.copy(loading = this.loading, loadingMsg = this.msg)

            is Dialog -> oldState.copy(
                loading = false,
                showingDialog = true,
                dialogContent = this.msg
            )

            is LoadMore -> oldState.copy(activities = oldState.activities+this.activities)
        }
    }

    data class Loading(val msg: String = "加载中", val loading: Boolean = true) : PageActions()
    data class Dialog(val msg: String) : PageActions()
    data class LoadMore(val activities: List<SCActivity>) : PageActions()
}