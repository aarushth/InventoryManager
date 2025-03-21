package com.leopardseal.inventorymanagerapp.ui.home.org

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import kotlinx.coroutines.launch

class OrgViewModel(
    private val repository: OrgRepository
) : ViewModel(){

    private val _orgResponse : MutableLiveData<Resource<List<Orgs>>> = MutableLiveData()
    val orgResponse: LiveData<Resource<List<Orgs>>>
        get() = _orgResponse

    fun getOrgs() = viewModelScope.launch {
        _orgResponse.value = repository.getOrgs() as Resource<List<Orgs>>
    }
}