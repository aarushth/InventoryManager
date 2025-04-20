package com.leopardseal.inventorymanagerapp.data

import android.content.Context
import android.graphics.Picture
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("app_preferences")
class UserPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

//    private val dataStore : DataStore<Preferences>
//    init{
//        dataStore = applicationContext.createDataStore(
//            name = "my_data_store"
//        )
//    }

    val authToken : Flow<String?>
        get() = appContext.dataStore.data.map { it[KEY_AUTH] }

    val orgId : Flow<Long?>
        get() = appContext.dataStore.data.map { it[ORG_ID] }

    val pictureUrl : Flow<String?>
        get() = appContext.dataStore.data.map { it[PICTURE_URL] }

    val orgImg : Flow<String?>
        get() = appContext.dataStore.data.map { it[ORG_IMG] }

    val orgName : Flow<String?>
        get() = appContext.dataStore.data.map { it[ORG_NAME] }



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


    suspend fun savePictureUrl(pictureUrl: String){
        appContext.dataStore.edit {
            it[PICTURE_URL] = pictureUrl
        }
    }

    companion object {
        private val KEY_AUTH = stringPreferencesKey("authToken")
        private val ORG_ID = longPreferencesKey("orgId")
        private val ORG_NAME = stringPreferencesKey("orgName")
        private val ORG_IMG = stringPreferencesKey("orgImg")
        private val PICTURE_URL = stringPreferencesKey("pictureUrl")
    }
}