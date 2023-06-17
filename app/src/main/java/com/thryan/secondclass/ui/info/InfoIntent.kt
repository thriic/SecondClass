package com.thryan.secondclass.ui.info

import java.time.LocalTime

sealed class InfoIntent {
    data class UpdateSignInTime(val signInTime: LocalTime) : InfoIntent()
    data class UpdateSignOutTime(val signOutTime: LocalTime) : InfoIntent()
    data class ShowDialog(val signInDialog: Boolean = true) : InfoIntent()
    object CloseDialog : InfoIntent()
    object Sign : InfoIntent()
    object SignIn : InfoIntent()
    object GenerateLink : InfoIntent()
}