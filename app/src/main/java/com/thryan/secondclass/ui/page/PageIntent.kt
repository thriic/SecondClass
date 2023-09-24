package com.thryan.secondclass.ui.page

sealed class PageIntent {
    object Init : PageIntent()
    object CloseDialog : PageIntent()
    object UpdateActivity : PageIntent()
    object LoadMore : PageIntent()
    object openUser : PageIntent()
    data class Search(val keyword: String) : PageIntent()
    data class ShowDialog(val message: String = "", val userInfo: Boolean = false) : PageIntent()
    data class OpenActivity(val id: String) : PageIntent()
}