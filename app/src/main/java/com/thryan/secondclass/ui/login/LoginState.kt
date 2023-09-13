package com.thryan.secondclass.ui.login

data class LoginState(
    val account: String,
    val password: String,
    val scPassword: String,
    val showDialog: Boolean,
    val message: String,
    val showPassword: Boolean,
    val pending: Boolean
)
