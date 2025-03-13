package com.leopardseal.inventorymanagerapp.ui.login

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.leopardseal.inventorymanagerapp.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.repositories.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel(){

    fun login() = viewModelScope.launch {
        repository.login()
    }
}