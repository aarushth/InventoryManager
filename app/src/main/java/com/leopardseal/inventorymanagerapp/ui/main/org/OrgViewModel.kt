package com.leopardseal.inventorymanagerapp.ui.main.org

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrgViewModel @Inject constructor(
    private val repository: OrgRepository
) : ViewModel(){

    private val _orgResponse = MutableStateFlow<Resource<List<Orgs>>>(Resource.Loading)
    val orgResponse: StateFlow<Resource<List<Orgs>>>
        get() = _orgResponse

    private val _orgSaved = MutableStateFlow<Boolean>(false)
    val orgSaved: StateFlow<Boolean> = _orgSaved

    init {
        getOrgs()
    }
    fun resetOrgSavedFlag() {
        _orgSaved.value = false
    }

    fun saveOrg(org: Orgs) = viewModelScope.launch{
        repository.saveOrg(org)
        _orgSaved.emit(true)
    }
    fun getOrgs() = viewModelScope.launch {
        _orgResponse.value = repository.getOrgs() as Resource<List<Orgs>>
    }

    
}