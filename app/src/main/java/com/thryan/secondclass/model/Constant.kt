package com.thryan.secondclass.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constant {
    const val DATA_STORE_NAME = "data"
    val KEY_TWFID = stringPreferencesKey("twfid")
    val KEY_ACCOUNT = stringPreferencesKey("account")
    val KEY_PASSWORD = stringPreferencesKey("password")
    val KEY_SC_PASSWORD = stringPreferencesKey("sc_password")
    val KEY_LAST_TIME = stringPreferencesKey("last_time")
    val KEY_DYNAMIC = booleanPreferencesKey("dynamic")
    val KEY_WEB_VIEW = booleanPreferencesKey("web_view")
    val KEY_RESIGN = booleanPreferencesKey("resign")
    val KEY_KEYWORD = stringPreferencesKey("keyword")
    val KEY_ONLY_SIGN= booleanPreferencesKey("only_sign")
    val KEY_STATUS = stringPreferencesKey("status")
    val KEY_TYPE = stringPreferencesKey("type")
    val KEY_EXCLUDE_CLASSES = booleanPreferencesKey("exclude_classes")
}