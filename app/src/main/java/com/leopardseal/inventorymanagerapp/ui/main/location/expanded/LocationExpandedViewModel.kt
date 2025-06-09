package com.leopardseal.inventorymanagerapp.ui.main.location.expanded


import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.BoxRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ImageRepository
import com.leopardseal.inventorymanagerapp.data.repositories.LocationRepository
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Location
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LocationExpandedViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val boxRepository: BoxRepository,
    private val imageRepository : ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val locationId: Long = savedStateHandle["location_id"] ?: -1L

    private val _location = MutableStateFlow<Location?>(repository.getCachedLocationById(locationId))
    val location: StateFlow<Location?>
        get() = _location

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _boxResource = MutableStateFlow<Resource<List<Boxes>>>(Resource.Init)
    val boxResource: StateFlow<Resource<List<Boxes>>>
        get() = _boxResource

    init {
        _location.value = repository.getCachedLocationById(locationId)
        getLocation()
    }
    
    fun getLocation(){
        _location.value = null
        _isRefreshing.value = true
        if(locationId >= 0 ) {
            viewModelScope.launch {
                val response = repository.fetchLocationById(locationId)
                if (response is Resource.Success) {
                    _location.value = response.value
                }
                getBoxes()
                _isRefreshing.value = false
            }
        }else {
            _isRefreshing.value = false
        }
    }

    fun getBoxes(){
        viewModelScope.launch {
            _boxResource.value = boxRepository.getBoxesByLocationId(locationId)
        }
    }
    fun saveOrUpdateLocation(updatedLocation: Location, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateLocation(updatedLocation, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedLocation.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _location.value = updatedLocation
            }

        } catch (e: Exception) {
            _updateResponse.value = Resource.Failure(isNetworkError = false, errorCode = null)
        }
    }
    private val _uploadResult = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val uploadResult: StateFlow<Resource<Unit>>
        get() = _uploadResult

    fun uploadImage(sasUrl: String, imageFile: File) {
        resetUpdateResponse()
        _uploadResult.value = Resource.Loading
        viewModelScope.launch {
            _uploadResult.value = imageRepository.uploadImage(sasUrl, imageFile)
        }
    }
    fun resetUploadFlag(){
        _uploadResult.value = Resource.Init
    }
    fun resetUpdateResponse(){
        _updateResponse.value = Resource.Init
    }

}