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

//    val userId : Flow<Long?>
//        get() = appContext.dataStore.data.map { it[USER_ID] }

    val pictureUrl : Flow<String?>
        get() = appContext.dataStore.data.map { it[PICTURE_URL] }
//    val authToken = [Keys.KEY_AUTH] ?: false


    suspend fun saveAuthToken(authToken: String){
        appContext.dataStore.edit {
            it[KEY_AUTH] = authToken
        }
    }

//    suspend fun saveUserId(userId: Long){
//        appContext.dataStore.edit {
//            it[USER_ID] = userId
//        }
//    }

    suspend fun savePictureUrl(pictureUrl: String){
        appContext.dataStore.edit {
            it[PICTURE_URL] = pictureUrl
        }
    }

    companion object {
        private val KEY_AUTH = stringPreferencesKey("authToken")
//        private val USER_ID = longPreferencesKey("userId")
        private val PICTURE_URL = stringPreferencesKey("pictureUrl")
    }
}