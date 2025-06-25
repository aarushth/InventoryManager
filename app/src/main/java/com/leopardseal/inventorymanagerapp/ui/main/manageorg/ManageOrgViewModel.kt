package com.leopardseal.inventorymanagerapp.ui.main.manageorg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ManageOrgRepository
import com.leopardseal.inventorymanagerapp.data.responses.Role
import com.leopardseal.inventorymanagerapp.data.responses.dto.ManageOrgsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageOrgViewModel @Inject constructor(
    private val repository : ManageOrgRepository
) : ViewModel(){

    private val _userResponse = MutableStateFlow<Resource<ManageOrgsResponse>>(Resource.Init)
    val userResponse : StateFlow<Resource<ManageOrgsResponse>>
        get() = _userResponse

    private val _removeResponse = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val removeResponse : StateFlow<Resource<Unit>>
        get() = _removeResponse

    private val _removeInviteResponse = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val removeInviteResponse : StateFlow<Resource<Unit>>
        get() = _removeInviteResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing : StateFlow<Boolean>
        get() = _isRefreshing

    private val _inviteResponse = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val inviteResponse : StateFlow<Resource<Unit>>
        get() = _inviteResponse

    init{
        _userResponse.value = Resource.Loading
        getUserList()
    }

    fun getUserList(){
        _isRefreshing.value = true
        viewModelScope.launch {
            _userResponse.value = repository.getUserList()
            _isRefreshing.value = false
        }
    }

    fun removeUser(userId : Long){
        viewModelScope.launch {
            _removeResponse.value = Resource.Loading
            _removeResponse.value = repository.removeUser(userId)
        }
    }

    fun resetRemoveFlag(){
        _removeResponse.value = Resource.Init
        getUserList()
    }

    fun invite(email : String, role: Role) {
        viewModelScope.launch {
            _inviteResponse.value = Resource.Loading
            _inviteResponse.value = repository.invite(email, role)
        }
    }

    fun resetInviteFlag(){
        _inviteResponse.value = Resource.Init
        getUserList()
    }

    fun removeInvite(userId : Long){
        viewModelScope.launch {
            _removeInviteResponse.value = Resource.Loading
            _removeInviteResponse.value = repository.removeInvite(userId)
        }
    }

    fun resetRemoveInviteFlag(){
        _removeInviteResponse.value = Resource.Init
        getUserList()
    }
}