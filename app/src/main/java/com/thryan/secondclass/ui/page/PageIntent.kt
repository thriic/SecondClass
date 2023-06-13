package com.thryan.secondclass.ui.page

sealed class PageIntent {
    object Init : PageIntent()
    object CloseDialog : PageIntent()
    data class ShowDialog(val type: Int) : PageIntent()
    data class OpenActivity(val id: String) : PageIntent()
}