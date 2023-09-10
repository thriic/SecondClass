package com.thryan.secondclass.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.thryan.secondclass.model.Constant.KEY_ACCOUNT
import com.thryan.secondclass.model.Constant.KEY_PASSWORD
import com.thryan.secondclass.model.Constant.KEY_TWFID
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
class AppDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun putAccount(value: String) {
        putString(KEY_ACCOUNT, value)
    }

    suspend fun putPassword(value: String) {
        putString(KEY_PASSWORD, value)
    }

    suspend fun putTwfid(value: String) {
        putString(KEY_TWFID, value)
    }


    fun getAccount(default: String): String = runBlocking {
        val string = getString(KEY_ACCOUNT)
        return@runBlocking string ?: default
    }

    fun getPassword(default: String): String = runBlocking {
        val string = getString(KEY_PASSWORD)
        return@runBlocking string ?: default
    }

    fun getTwfid(default: String): String = runBlocking {
        val string = getString(KEY_TWFID)
        return@runBlocking string ?: default
    }

    private suspend fun putString(key: Preferences.Key<String>, value: String) = dataStore.edit {
        it[key] = value
    }

    private fun getString(key: Preferences.Key<String>): String? = runBlocking {
        return@runBlocking dataStore.data.map {
            it[key]
        }.first()
    }

}