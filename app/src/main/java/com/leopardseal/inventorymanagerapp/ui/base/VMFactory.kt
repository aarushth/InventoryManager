package com.leopardseal.inventorymanagerapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leopardseal.inventorymanagerapp.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.ui.login.LoginViewModel

class VMFactory(
    private val repository: BaseRepository
) :ViewModelProvider.NewInstanceFactory(){

    override fun<T : ViewModel> create(modelClass: Class<T>): T{
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as LoginRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class not found :(")
        }
    }
}