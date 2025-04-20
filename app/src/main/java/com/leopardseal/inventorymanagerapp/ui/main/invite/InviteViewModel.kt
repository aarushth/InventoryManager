package com.leopardseal.inventorymanagerapp.ui.main.invite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.InviteRepository
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import kotlinx.coroutines.launch

class InviteViewModel(
        private val repository: InviteRepository
) : ViewModel(){

    private val _inviteResponse : MutableLiveData<Resource<List<Orgs>>> = MutableLiveData()
    val inviteResponse: LiveData<Resource<List<Orgs>>>
    get() = _inviteResponse

    fun getInvites() = viewModelScope.launch {
        _inviteResponse.value = repository.getInvites() as Resource<List<Orgs>>
    }

    private val _acceptResponse : MutableLiveData<Resource<String>> = MutableLiveData()
    val acceptResponse: LiveData<Resource<String>>
        get() = _acceptResponse
    fun acceptInvite(orgId : Long, role : String) = viewModelScope.launch {
        _acceptResponse.value = repository.acceptInvite(orgId, role) as Resource<String>
    }
}