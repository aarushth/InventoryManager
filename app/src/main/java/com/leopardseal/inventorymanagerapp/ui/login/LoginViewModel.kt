package com.leopardseal.inventorymanagerapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.responses.MyUsers
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel(){

    private val _loginResponse : MutableLiveData<Resource<MyUsers>> = MutableLiveData()
    val loginResponse: LiveData<Resource<MyUsers>>
        get() = _loginResponse


    fun saveAuthToken(authToken: String) = viewModelScope.launch{
        repository.saveAuthToken(authToken)
    }

//    fun saveUserId(userId: Long) = viewModelScope.launch{
//        repository.saveUserId(userId)
//    }
    fun savePictureUrl(pictureUrl: String) = viewModelScope.launch{
        repository.savePictureUrl(pictureUrl)
    }

    fun login(authToken : String) = viewModelScope.launch {
        _loginResponse.value = repository.login(authToken) as Resource<MyUsers>
    }
}