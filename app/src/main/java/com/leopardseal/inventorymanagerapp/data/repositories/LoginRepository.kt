package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.LoginAPI
import javax.inject.Inject


class LoginRepository @Inject constructor(
    private val api: LoginAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun login(authToken : String) = safeApiCall {
        api.login(authToken)
    }

//    suspend fun saveUserId(userId: Long){
//        preferences.saveUserId(userId)
//    }
    suspend fun saveUserImg(userImg: String){
        preferences.savePictureUrl(userImg)
    }
    suspend fun saveToken(token: String){
        preferences.saveAuthToken(token)
    }
}