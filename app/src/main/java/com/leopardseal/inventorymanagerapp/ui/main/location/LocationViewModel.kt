package com.leopardseal.inventorymanagerapp.ui.main.location


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.LocationRepository
import com.leopardseal.inventorymanagerapp.data.responses.Locations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel(){

    private val _locationResponse = MutableStateFlow<Resource<List<Locations>>>(Resource.Loading)
    val locationResponse: StateFlow<Resource<List<Locations>>>
        get() = _locationResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    fun getLocations() = viewModelScope.launch {
        _isRefreshing = true
        _locationResponse.value = repository.getLocations() as Resource<List<Locations>>

        if (response is Resource.Success) {
            repository.setCachedLocations(response.value)
        }
        _isRefreshing = false
    }

}