package com.thryan.secondclass.ui.page

sealed class PageIntent {
    class Init(val login: Boolean) : PageIntent()
    object CloseDialog : PageIntent()
    object UpdateActivity : PageIntent()
    object LoadMore : PageIntent()
    object OpenUser : PageIntent()
    data class Search(
        val keyword: String = "",
        val onlySign: Boolean = false,
        val status: String = "",
        val type: String = "",
        val excludeClasses: Boolean = false
    ) : PageIntent()

    data class ShowDialog(val message: String = "", val userInfo: Boolean = false) : PageIntent()
    data class OpenActivity(val id: String) : PageIntent()
}