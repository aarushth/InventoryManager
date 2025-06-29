package com.leopardseal.inventorymanagerapp.ui.login


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.responses.dto.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel(){

    private val _loginResponse = MutableStateFlow<Resource<LoginResponse>>(Resource.Init)
    val loginResponse: StateFlow<Resource<LoginResponse>>
        get() = _loginResponse



    private val _tokenSaved = MutableStateFlow(false)
    val tokenSaved: StateFlow<Boolean> = _tokenSaved

    private val _showDeletePopup = MutableStateFlow(false)
    val showDeletePopup: StateFlow<Boolean> = _showDeletePopup

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted

    fun getEmail() = runBlocking {
        repository.getEmail()
    }
    fun resetTokenSavedFlag() {
        _tokenSaved.value = false
    }
    fun resetLoginResponse(){
        _loginResponse.value = Resource.Init
    }

    private fun login(authToken : String) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        try {
            val response = repository.login(authToken)

            _loginResponse.value = response
            if(response is Resource.Success) {
                repository.saveToken((_loginResponse.value as Resource.Success<LoginResponse>).value.token)
                if(response.value.toDelete) {
                    _showDeletePopup.value = true
                }else{
                    savedetails()
                }
            }else{
                repository.clear()
            }

        } catch (e: Exception) {
            _loginResponse.value = Resource.Failure(
                isNetworkError = e is IOException,
                errorCode = (e as? HttpException)?.code())
        }
    }
    fun savedetails() = viewModelScope.launch{
        _showDeletePopup.value = false
        repository.saveUserEmail((_loginResponse.value as Resource.Success<LoginResponse>).value.user.email)

        (_loginResponse.value as Resource.Success<LoginResponse>).value.user.imgUrl?.let {
            repository.saveUserImg(it)
        }
        _tokenSaved.value = true
    }
    fun triggerLogin(context: Context, autoSelect : Boolean) {
        Log.e("Login", "autoSelect $autoSelect")
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
                    Log.e("Login", "success $token")
                    login(token)
                }
            } catch (e: GetCredentialException) {
                Log.e("Login", "Login failed", e)
                _loginResponse.value = Resource.Failure(
                    isNetworkError = e is IOException,
                    errorCode = (e as? HttpException)?.code())
            }
        }
    }
    fun confirmAccountDeletion() = viewModelScope.launch{
        _showDeletePopup.value = false
        val response = repository.confirmDelete()
        if(response is Resource.Success) {
            repository.clear()
            _tokenSaved.value = false
            _loginResponse.value = Resource.Init
            _deleted.value = true
        }
    }
    fun cancelDeletion() = viewModelScope.launch{
        _showDeletePopup.value = false
        val response = repository.cancelDelete()
        if(response is Resource.Success){
            savedetails()
        }
    }
    fun resetDeleted() {
        _deleted.value = false
    }
}