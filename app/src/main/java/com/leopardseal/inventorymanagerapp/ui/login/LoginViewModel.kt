package com.leopardseal.inventorymanagerapp.ui.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.leopardseal.inventorymanagerapp.data.UserPreferences


import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.responses.dto.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val userPreferences: UserPreferences
) : ViewModel(){

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse



    private val _tokenSaved = MutableStateFlow(false)
    val tokenSaved: StateFlow<Boolean> = _tokenSaved

    fun getEmail() = runBlocking {
        userPreferences.userEmail.first()
    }
    fun resetTokenSavedFlag() {
        _tokenSaved.value = false
    }

    private fun login(authToken : String) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        try {
            val response = repository.login(authToken)

            _loginResponse.value = response
            if(response is Resource.Success) {
                userPreferences.saveUserEmail(response.value.user.email)
                userPreferences.saveAuthToken(response.value.token)
                userPreferences.saveUserEmail(response.value.user.email)
                response.value.user.imgUrl?.let {
                    userPreferences.saveUserImg(it)
                }
            }
            _tokenSaved.value = true
        } catch (e: Exception) {
            _loginResponse.value = Resource.Failure(
                isNetworkError = e is IOException,
                errorCode = (e as? HttpException)?.code())
        }
    }
    fun triggerLogin(context: Context, autoSelect : Boolean) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(autoSelect)
            .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
            .build()

        val credentialManager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                if (result.credential is CustomCredential &&result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val token = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                    login(token)
                }
            } catch (e: GetCredentialException) {
                Log.e("Login", "Login failed", e)
            }
        }
    }
}