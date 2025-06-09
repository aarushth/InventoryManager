package com.leopardseal.inventorymanagerapp.ui.main.box.expanded


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.BoxRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ImageRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.repositories.LocationRepository
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.data.responses.Locations
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BoxExpandedViewModel @Inject constructor(
    private val repository: BoxRepository,
    private val itemRepository: ItemRepository,
    private val locationRepository: LocationRepository,
    private val imageRepository : ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val boxId: Long = savedStateHandle["box_id"] ?: -1L
    private val locationIdFlow = savedStateHandle.getStateFlow<Long?>("location_id", null)

    private val _box = MutableStateFlow<Box?>(repository.getCachedBoxById(boxId))
    val box: StateFlow<Box?>
        get() = _box

    private val _location = MutableStateFlow<Locations?>(null)
    val location: StateFlow<Locations?>
        get() = _location


    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    private val _itemResource = MutableStateFlow<Resource<List<Items>>>(Resource.Init)
    val itemResource: StateFlow<Resource<List<Items>>>
        get() = _itemResource

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    fun setLocationIdIfNotPresent(id: Long?) {
        savedStateHandle["location_id"] = id
    }
    init {
        _box.value = repository.getCachedBoxById(boxId)
        getBox()

        if (savedStateHandle.get<Long>("location_id") == null) {
            val boxLocationId = _box.value?.locationId
            if (boxLocationId != null) {
                savedStateHandle["location_id"] = boxLocationId
            }
        }
        viewModelScope.launch {
            locationIdFlow.collect { locationId ->
                getLocation(locationId)
            }
        }
    }
    fun setLocationId(id: Long) {
        savedStateHandle["location_id"] = id
    }
    fun getBox(){
        _box.value = null
        _isRefreshing.value = true
        if(boxId >= 0) {
            viewModelScope.launch {
                val response = repository.fetchBoxById(boxId)
                if (response is Resource.Success) {
                    _box.value = response.value
                }
                getItems()
                _isRefreshing.value = false
            }
        }else{
            _isRefreshing.value = false
        }
    }
    private fun getLocation(locationId : Long?){
        if(locationId != null) {
            _isRefreshing.value = true
            viewModelScope.launch {
                val response = locationRepository.fetchLocationById(locationId)
                if (response is Resource.Success) {
                    _location.value = response.value
                }
                _isRefreshing.value = false
            }
        }
    }

    private fun getItems(){
        viewModelScope.launch {
            _itemResource.value = itemRepository.getItemsByBoxId(boxId)
        }
    }
    fun updateBoxLoc() = viewModelScope.launch {
        val newBox = _box.value!!.copy(locationId = locationIdFlow.value)

        _updateResponse.value = repository.updateBox(newBox, false)
        _box.value = newBox
        savedStateHandle["location_id"] = newBox.locationId
    }
    fun saveOrUpdateBox(updatedBox: Box, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateBox(updatedBox, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedBox.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _box.value = updatedBox
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