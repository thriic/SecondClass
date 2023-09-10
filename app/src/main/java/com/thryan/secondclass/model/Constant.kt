package com.thryan.secondclass.model

import androidx.datastore.preferences.core.stringPreferencesKey

object Constant {
    const val DATA_STORE_NAME = "data"
    val KEY_TWFID = stringPreferencesKey("twfid")
    val KEY_ACCOUNT = stringPreferencesKey("account")
    val KEY_PASSWORD = stringPreferencesKey("password")
}