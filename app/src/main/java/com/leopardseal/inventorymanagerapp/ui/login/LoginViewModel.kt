package com.leopardseal.inventorymanagerapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.leopardseal.inventorymanagerapp.network.Resource

import com.leopardseal.inventorymanagerapp.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.responses.LoginResponse
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel(){

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse



    fun login() = viewModelScope.launch {
        _loginResponse.value = repository.login() as Resource<LoginResponse>
    }
}