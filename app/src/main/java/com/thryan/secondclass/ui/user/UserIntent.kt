package com.thryan.secondclass.ui.user

sealed class UserIntent {
    data class ChangeDynamic(val checked: Boolean) : UserIntent()
    data class ChangeWebView(val checked: Boolean) : UserIntent()
    object Dialog : UserIntent()
}