package com.leopardseal.inventorymanagerapp.ui.main.manageorg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageOrgViewModel @Inject constructor(
    private val repository : ManageOrgRepository
) : ViewModel(){

    private val _userResponse = MutableStateFlow<Resource<List<UserResponse>>(Resource.Init)
    val userResponse : StateFlow<Resource<List<UserResponse>>
        get() = _userResponse

    init{
        _userResponse.value = Resource.Loading
        getUserList()
    }

    fun getUserList(){
        viewModelScope.launch {
            _userResponse.value = repository.getUserList()
        }
    }
}