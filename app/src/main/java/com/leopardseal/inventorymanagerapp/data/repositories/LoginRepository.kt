package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.LoginAPI

class LoginRepository(
    private val api: LoginAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun login( authToken : String) = safeApiCall {
        api.login(authToken)
    }

    suspend fun saveAuthToken(authToken: String){
        preferences.saveAuthToken(authToken)
    }
//    suspend fun saveUserId(userId: Long){
//        preferences.saveUserId(userId)
//    }
    suspend fun savePictureUrl(pictureUrl: String){
        preferences.savePictureUrl(pictureUrl)
    }

}