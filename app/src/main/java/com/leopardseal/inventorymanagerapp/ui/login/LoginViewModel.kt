package com.leopardseal.inventorymanagerapp.ui.login

import androidx.lifecycle.ViewModel
import com.leopardseal.inventorymanagerapp.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.repositories.LoginRepository

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel(){
}