package com.leopardseal.inventorymanagerapp.ui.main.location


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.LocationRepository
import com.leopardseal.inventorymanagerapp.data.responses.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel(){

    private val _locationResponse = MutableStateFlow<Resource<List<Location>>>(Resource.Loading)
    val locationResponse: StateFlow<Resource<List<Location>>>
        get() = _locationResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    init {
        if (repository.isLocationListFullyCached()) {
            getLocations()
        } else {
            fetchLocations()
        }
    }
    fun getLocations(){
        val cachedLocation = repository.getCachedLocations()
        if(cachedLocation is Resource.Failure){
            fetchLocations()
        }else{
            _locationResponse.value = cachedLocation
        }
    }
    fun fetchLocations() = viewModelScope.launch {
        _isRefreshing.value = true
        val response = repository.getLocations()
        _locationResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedLocations(response.value)
        }
        _isRefreshing.value = false
    }

}