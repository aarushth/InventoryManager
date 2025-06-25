package com.leopardseal.inventorymanagerapp.ui.main.setting

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
class SettingViewModel @Inject constructor(
    private val repository : SettingRepository
) : ViewModel(){

    private val _versionResponse = MutableStateFlow<Resource<String>>(Resource.Init)
    val versionResponse : StateFlow<Resource<String>>
        get() = _versionResponse

    init{
        _versionResponse.value = Resource.Loading
        getVersion()
    }

    fun getVersion(){
        viewModelScope.launch {
            _versionResponse.value = repository.getVersion()
        }
    }

}