package com.thryan.secondclass.ui.login

sealed class LoginIntent {
    object Init : LoginIntent()
    object Login : LoginIntent()
    object GetPreference : LoginIntent()
    object CloseDialog : LoginIntent()
    object ChangeWebView : LoginIntent()
    data class UpdateAccount(val account: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data class UpdateSCAccount(val scAccount: String) : LoginIntent()
    data class UpdatePasswordVisible(val visible: Boolean) : LoginIntent()
    data class UpdatePending(val isPending: Boolean) : LoginIntent()
    data class WebLogin(val twfid: String, val token: String) : LoginIntent()
}