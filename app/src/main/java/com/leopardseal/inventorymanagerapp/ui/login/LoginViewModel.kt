package com.leopardseal.inventorymanagerapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.responses.dto.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel(){

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse




//    fun saveUserId(userId: Long) = viewModelScope.launch{
//        repository.saveUserId(userId)
//    }
    private val _tokenSaved = MutableStateFlow(false)
    val tokenSaved: StateFlow<Boolean> = _tokenSaved

    fun resetTokenSavedFlag() {
        _tokenSaved.value = false
    }

    fun saveToken(token: String) = viewModelScope.launch{
        repository.saveToken(token)
        _tokenSaved.emit(true)
    }
    fun savePictureUrl(pictureUrl: String) = viewModelScope.launch{
        repository.savePictureUrl(pictureUrl)
    }

    fun login(authToken : String) = viewModelScope.launch {
        _loginResponse.value = repository.login(authToken) as Resource<LoginResponse>
    }
}