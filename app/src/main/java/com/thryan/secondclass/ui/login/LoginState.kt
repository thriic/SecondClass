package com.thryan.secondclass.ui.login

data class LoginState(
    val account: String,
    val password: String,
    val scAccount: String,
    val showDialog: Boolean,
    val message: String
)
