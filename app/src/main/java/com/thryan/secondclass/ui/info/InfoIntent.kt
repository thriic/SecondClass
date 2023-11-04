package com.thryan.secondclass.ui.info

import java.time.LocalDate
import java.time.LocalTime

sealed class InfoIntent {
    data class UpdateSignInTime(val signInTime: LocalTime) : InfoIntent()
    data class UpdateSignOutTime(val signOutTime: LocalTime) : InfoIntent()
    data class UpdateSignInDate(val signInDate: LocalDate) : InfoIntent()
    data class UpdateSignOutDate(val signOutDate: LocalDate) : InfoIntent()
    data class ShowDialog(val dialogType: Dialog) : InfoIntent()
    object CloseDialog : InfoIntent()
    object Sign : InfoIntent()
    object SignIn : InfoIntent()
    object GenerateLink : InfoIntent()
}