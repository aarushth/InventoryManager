package com.leopardseal.inventorymanagerapp.ui.main.org

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.BoxRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.repositories.LocationRepository
import com.leopardseal.inventorymanagerapp.data.repositories.OrgRepository
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Org
import com.leopardseal.inventorymanagerapp.data.responses.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrgViewModel @Inject constructor(
    private val repository: OrgRepository,
    private val itemRepository: ItemRepository,
    private val boxRepository: BoxRepository,
    private val locationRepository: LocationRepository
) : ViewModel(){

    private val _orgResponse = MutableStateFlow<Resource<List<Org>>>(Resource.Loading)
    val orgResponse: StateFlow<Resource<List<Org>>>
        get() = _orgResponse

    private val _orgSaved = MutableStateFlow<Boolean>(false)
    val orgSaved: StateFlow<Boolean> = _orgSaved

    init {
        getOrgs()
    }
    fun resetOrgSavedFlag() {
        _orgSaved.value = false
    }

    fun saveOrg(userRole: UserRole) = viewModelScope.launch{
        repository.saveOrg(userRole)
        itemRepository.clearCache()
        boxRepository.clearCache()
        locationRepository.clearCache()
        _orgSaved.emit(true)
    }
    fun getOrgs() = viewModelScope.launch {
        _orgResponse.value = repository.getOrgs() as Resource<List<Org>>
    }

    
}