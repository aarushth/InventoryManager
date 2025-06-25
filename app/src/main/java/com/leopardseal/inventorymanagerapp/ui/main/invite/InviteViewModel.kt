package com.leopardseal.inventorymanagerapp.ui.main.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.InviteRepository
import com.leopardseal.inventorymanagerapp.data.responses.Invite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
        private val repository: InviteRepository
) : ViewModel(){

    private val _inviteResponse = MutableStateFlow<Resource<List<Invite>>>(Resource.Loading)
    val inviteResponse: StateFlow<Resource<List<Invite>>>
    get() = _inviteResponse

    fun getInvites() = viewModelScope.launch {
        _inviteResponse.value = repository.getInvites()
    }
    init {
        getInvites()
    }
    private val _acceptResponse = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val acceptResponse: StateFlow<Resource<Unit>>
        get() = _acceptResponse

    fun acceptInvite(invite : Invite) = viewModelScope.launch {
        _acceptResponse.value = repository.acceptInvite(invite.org.id, invite.role.role)
    }

    fun resetAcceptResponse() = viewModelScope.launch {
        _acceptResponse.value = Resource.Init
    }
}