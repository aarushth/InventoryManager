package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.LoginAPI
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class LoginRepository @Inject constructor(
    private val api: LoginAPI,
    private val preferences: UserPreferences
): BaseRepository() {

    suspend fun login(authToken : String) = safeApiCall {
        api.login(authToken)
    }

    suspend fun saveUserEmail(userEmail: String){
        preferences.saveUserEmail(userEmail)
    }
    suspend fun saveUserImg(userImg: String){
        preferences.saveUserImg(userImg)
    }
    suspend fun saveToken(token: String){
        preferences.saveAuthToken(token)
    }
    suspend fun getEmail(): String? {
        return preferences.userEmail.first()
    }
    suspend fun clear(){
        preferences.clear()
    }
    suspend fun confirmDelete() = safeApiCall{
        api.confirmDelete()
    }
    suspend fun cancelDelete() = safeApiCall{
        api.cancelDelete()
    }
}