package com.thryan.secondclass.ui.login

sealed class LoginIntent {
    object Login : LoginIntent()
    object GetPreference : LoginIntent()
    object CloseDialog : LoginIntent()
    data class UpdateAccount(val account: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data class UpdateSCAccount(val scAccount: String) : LoginIntent()
}