package com.leopardseal.inventorymanagerapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leopardseal.inventorymanagerapp.data.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository
import com.leopardseal.inventorymanagerapp.ui.home.org.OrgViewModel
import com.leopardseal.inventorymanagerapp.ui.login.LoginViewModel

class VMFactory(
    private val repository: BaseRepository
) :ViewModelProvider.NewInstanceFactory(){

    override fun<T : ViewModel> create(modelClass: Class<T>): T{

        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as LoginRepository) as T
            modelClass.isAssignableFrom(OrgViewModel::class.java) -> OrgViewModel(repository as OrgRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class not found :(")
        }
    }
}