package com.leopardseal.inventorymanagerapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leopardseal.inventorymanagerapp.data.repositories.BaseRepository
import com.leopardseal.inventorymanagerapp.data.repositories.InviteRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.repositories.LoginRepository
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository
import com.leopardseal.inventorymanagerapp.ui.main.invite.InviteViewModel
import com.leopardseal.inventorymanagerapp.ui.main.org.OrgViewModel
import com.leopardseal.inventorymanagerapp.ui.login.LoginViewModel
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemViewModel

class VMFactory(
    private val repository: BaseRepository
) :ViewModelProvider.NewInstanceFactory(){

    override fun<T : ViewModel> create(modelClass: Class<T>): T{

        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as LoginRepository) as T
            modelClass.isAssignableFrom(OrgViewModel::class.java) -> OrgViewModel(repository as OrgRepository) as T
            modelClass.isAssignableFrom(InviteViewModel::class.java) -> InviteViewModel(repository as InviteRepository) as T
            modelClass.isAssignableFrom(ItemViewModel::class.java) -> ItemViewModel(repository as ItemRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class not found :(")
        }
    }
}