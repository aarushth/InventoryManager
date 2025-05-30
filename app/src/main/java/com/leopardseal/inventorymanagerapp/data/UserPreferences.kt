package com.leopardseal.inventorymanagerapp.data

import android.content.Context

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("app_preferences")
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext

    suspend fun clear() {
        appContext.dataStore.edit { it.clear() }
    }

    val authToken : Flow<String?>
        get() = appContext.dataStore.data.map { it[KEY_AUTH] }

    val orgId : Flow<Long?>
        get() = appContext.dataStore.data.map { it[ORG_ID] }

    val userImg : Flow<String?>
        get() = appContext.dataStore.data.map { it[USER_IMG] }

    val orgImg : Flow<String?>
        get() = appContext.dataStore.data.map { it[ORG_IMG] }

    val orgName : Flow<String?>
        get() = appContext.dataStore.data.map { it[ORG_NAME] }

    val userEmail : Flow<String?>
        get() = appContext.dataStore.data.map { it[USER_EMAIL] }

    val orgRole : Flow<String?>
        get() = appContext.dataStore.data.map { it[ORG_ROLE] }

    suspend fun saveAuthToken(authToken: String){
        appContext.dataStore.edit {
            it[KEY_AUTH] = authToken
        }
    }

    suspend fun saveOrgId(orgId: Long){
        appContext.dataStore.edit {
            it[ORG_ID] = orgId
        }
    }
    suspend fun saveOrgImg(orgImg: String){
        appContext.dataStore.edit {
            it[ORG_IMG] = orgImg
        }
    }
    suspend fun saveOrgName(orgName: String){
        appContext.dataStore.edit {
            it[ORG_NAME] = orgName
        }
    }
    suspend fun orgRole(orgRole : String){
        appContext.dataStore.edit {
            it[ORG_ROLE] = orgRole
        }
    }
    suspend fun saveUserImg(userImg: String){
        appContext.dataStore.edit {
            it[USER_IMG] = userImg
        }
    }
    suspend fun saveUserEmail(userEmail: String){
        appContext.dataStore.edit {
            it[USER_EMAIL] = userEmail
        }
    }

    companion object {
        private val KEY_AUTH = stringPreferencesKey("authToken")
        private val ORG_ID = longPreferencesKey("orgId")
        private val ORG_NAME = stringPreferencesKey("orgName")
        private val ORG_ROLE = stringPreferencesKey("orgRole")
        private val ORG_IMG = stringPreferencesKey("orgImg")
        private val USER_IMG = stringPreferencesKey("userImg")
        private val USER_EMAIL = stringPreferencesKey("userEmail")
    }
}