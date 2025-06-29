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
class BoxExpandedViewModel @Inject constructor(
    private val repository: BoxRepository,
    private val itemRepository: ItemRepository,
    private val locationRepository: LocationRepository,
    private val imageRepository : ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val boxId: Long = savedStateHandle["box_id"] ?: -1L

    private val _box = MutableStateFlow<Box?>(repository.getCachedBoxById(boxId))
    val box: StateFlow<Box?>
        get() = _box

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?>
        get() = _location

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    private val _itemResource = MutableStateFlow<Resource<List<Item>>>(Resource.Init)
    val itemResource: StateFlow<Resource<List<Item>>>
        get() = _itemResource

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _newLocationId = MutableStateFlow<Long>(-1L)
    val newLocationId: StateFlow<Long>
        get() = _newLocationId

    init {



    }
    fun getBox(){
        _box.value = repository.getCachedBoxById(boxId)
        if(_box.value == null){
            fetchBox(false)
        }else{
            getCacheItems()
        }
    }
    fun saveEnable() : Boolean{
        return newLocationId.value != -1L && newLocationId.value != box.value?.locationId
    }
    fun setLocationIdIfNotPresent(id: Long) {
        _newLocationId.value = id
        getLocation(_newLocationId.value)
    }
    fun fetchBox(forceRefresh: Boolean){
        _box.value = null
        _isRefreshing.value = true
        if(boxId >= 0) {
            viewModelScope.launch {
                val response = repository.fetchBoxById(boxId)
                if (response is Resource.Success) {
                    _box.value = response.value
                    repository.updateCachedBox(response.value)
                }
                if(!forceRefresh) {
                    getCacheItems()
                    if(_newLocationId.value == -1L){
                        getCacheLocation(_box.value?.locationId)
                    }else{
                        getCacheLocation(_newLocationId.value)
                    }
                }else{
                    getItems()
                    getLocation(_box.value?.locationId)
                }
                _isRefreshing.value = false
            }
        }else{
            _isRefreshing.value = false
        }
    }
    private fun getCacheLocation(locationId : Long?){
        if(locationId != null) {
            _isRefreshing.value = true
            _location.value = locationRepository.getCachedLocationById(locationId)
            if(_location.value == null) {
                getLocation(locationId)
            }else{
                _isRefreshing.value = false
            }
        }
    }
    private fun getLocation(locationId: Long?){
        if(locationId != null) {
            _isRefreshing.value = true
            viewModelScope.launch {
                val response = locationRepository.fetchLocationById(locationId)
                if (response is Resource.Success) {
                    _location.value = response.value
                    locationRepository.updateCachedLocation(response.value)
                }
                _isRefreshing.value = false
            }
        }


    }

    private fun getCacheItems(){
        val cacheItems = itemRepository.getCachedItemsByBoxId(boxId)
        if(cacheItems.isEmpty()) {
            getItems()
        }
        else{
            _itemResource.value = Resource.Success(cacheItems)
        }
    }
    private fun getItems(){
        viewModelScope.launch {
            _itemResource.value = itemRepository.getItemsByBoxId(boxId)
        }
    }
    fun updateBoxLoc() = viewModelScope.launch {
        val newBox = _box.value!!.copy(locationId = newLocationId.value)

        _updateResponse.value = repository.updateBox(newBox, false)
        if(_updateResponse.value is Resource.Success){
            repository.updateCachedBox(newBox)
            _box.value = newBox
            _newLocationId.value = -1L
        }

    }
    fun saveOrUpdateBox(updatedBox: Box, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateBox(updatedBox, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedBox.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _box.value = updatedBox
                _newLocationId.value = -1L
                repository.updateCachedBox(updatedBox)
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