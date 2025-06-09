package com.leopardseal.inventorymanagerapp.ui.main.item.expanded

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ItemExpandedViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val boxRepository: BoxRepository,
    private val locationRepository: LocationRepository,
    private val imageRepository : ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Long = savedStateHandle["item_id"] ?: -1L
    private val boxIdFlow = savedStateHandle.getStateFlow<Long?>("box_id", null)
    private var lastLocationId: Long? = null

    private val _item = MutableStateFlow<Item?>(repository.getCachedItemById(itemId))
    val item: StateFlow<Item?>
        get() = _item

    private val _box = MutableStateFlow<Boxes?>(null)
    val box: StateFlow<Boxes?>
        get() = _box

    private val _location = MutableStateFlow<Locations?>(null)
    val location: StateFlow<Locations?>
        get() = _location

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    fun setBoxIdIfNotPresent(id: Long?) {
        savedStateHandle["box_id"] = id
    }
    init {
        _item.value = repository.getCachedItemById(itemId)
        getItem()

        if (savedStateHandle.get<Long>("box_id") == null) {
            val itemBoxId = _item.value?.boxId
            if (itemBoxId != null) {
                savedStateHandle["box_id"] = itemBoxId
            }
        }
        viewModelScope.launch {
            boxIdFlow.collect { boxId ->
                getBox(boxId)
            }
        }
        viewModelScope.launch {
            _box.filterNotNull().collect { newBox ->
                val newLocationId = newBox.locationId
                if (newLocationId != null && newLocationId != lastLocationId) {
                    lastLocationId = newLocationId
                    getLocation(newLocationId)
                }
            }
        }
    }
    fun setBoxId(id: Long) {
        savedStateHandle["box_id"] = id
    }
    fun getItem() {
        _item.value = null
        _isRefreshing.value = true
        if (itemId >= 0) {
            viewModelScope.launch {
                val response = repository.fetchItemById(itemId)
                if (response is Resource.Success) {
                    _item.value = response.value

                    val newBoxId = response.value.boxId
                    if (newBoxId != null) {
                        savedStateHandle["box_id"] = newBoxId

                        // fetch box and location every time
                        val boxResponse = boxRepository.fetchBoxById(newBoxId)
                        if (boxResponse is Resource.Success) {
                            _box.value = boxResponse.value
                            val locationId = boxResponse.value.locationId
                            if (locationId != null) {
                                getLocation(locationId)
                            }
                        }
                    }
                }
                _isRefreshing.value = false
            }
        } else {
            _isRefreshing.value = false
        }
    }
    private fun getBox(boxId : Long?){
        if(boxId != null) {
            _isRefreshing.value = true
            viewModelScope.launch {
                val response = boxRepository.fetchBoxById(boxId)
                if (response is Resource.Success) {
                    _box.value = response.value
                    if (box.value!!.locationId != null) {
                        _isRefreshing.value = false
                        getLocation(box.value!!.locationId!!)
                    }
                }else {
                    _isRefreshing.value = false
                }
            }
        }
    }
    private fun getLocation(locationId : Long){
        if(locationId >= 0 && !_isRefreshing.value) {
            _isRefreshing.value = true
            viewModelScope.launch {
                val response = locationRepository.fetchLocationById(locationId)
                if (response is Resource.Success) {
                    _location.value = response.value
                }
                _isRefreshing.value = false
            }
        }else{
            _isRefreshing.value = false
        }
    }

    fun updateItemQuantity(newQuantity: Long) = viewModelScope.launch {
        val newItem = _item.value!!.copy(quantity = newQuantity, boxId = boxIdFlow.value)

        _updateResponse.value = repository.updateItem(newItem, false)
        _item.value = newItem
        savedStateHandle["box_id"] = newItem.boxId
    }
    fun saveOrUpdateItem(updatedItem: Item, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateItem(updatedItem, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedItem.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _item.value = updatedItem
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