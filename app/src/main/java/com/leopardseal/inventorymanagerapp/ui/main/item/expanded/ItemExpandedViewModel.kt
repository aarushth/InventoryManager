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
//    private val boxIdFlow = savedStateHandle.getStateFlow<Long?>("box_id", null)
//    private var lastLocationId: Long? = null

    private val _item = MutableStateFlow<Item?>(repository.getCachedItemById(itemId))
    val item: StateFlow<Item?>
        get() = _item

    private val _box = MutableStateFlow<Box?>(null)
    val box: StateFlow<Box?>
        get() = _box

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?>
        get() = _location

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _newBoxId = MutableStateFlow<Long>(-1L)
    val newBoxId: StateFlow<Long>
        get() = _newBoxId

    fun setBoxIdIfNotPresent(id: Long) {
        _newBoxId.value = id
        getBox(_newBoxId.value)
    }
    init {


//        if (savedStateHandle.get<Long>("box_id") == null) {
//            val itemBoxId = _item.value?.boxId
//            if (itemBoxId != null) {
//                savedStateHandle["box_id"] = itemBoxId
//            }
//        }
//        viewModelScope.launch {
//            boxIdFlow.collect { boxId ->
//                if(boxId != null) {
//                    if (box.value != null) {
//                        getBox(boxId)
//                    } else {
//                        fetchBox(boxId, true)
//                    }
//                }
//            }
//        }
//        viewModelScope.launch {
//            _box.filterNotNull().collect { newBox ->
//                val newLocationId = newBox.locationId
//                if (newLocationId != null && newLocationId != lastLocationId) {
//                    lastLocationId = newLocationId
//                    getLocation(newLocationId)
//                }
//            }
//        }
    }

    fun getItem(){
        _item.value = repository.getCachedItemById(itemId)
        if(_item.value == null) {
            fetchItem(false)
        }else{
            if(_item.value!!.boxId != null) {
                getBox(_item.value!!.boxId!!)
            }
        }
    }
    fun fetchItem(refresh : Boolean) {
        _item.value = null
        _isRefreshing.value = true
        if (itemId >= 0) {
            viewModelScope.launch {
                val response = repository.fetchItemById(itemId)
                if (response is Resource.Success) {
                    _item.value = response.value
                    if(refresh){
                        _newBoxId.value = -1L
                    }
                    if(_item.value!!.boxId != null){
                        if(refresh){
                            fetchBox(_item.value!!.boxId, true)
                        }else{
                            getBox(_item.value!!.boxId)
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
            _box.value = boxRepository.getCachedBoxById(boxId)
            if(_box.value == null){
                fetchBox(boxId, false)
            }else if(_box.value!!.locationId != null){
                getLocation(_box.value!!.locationId!!)
            }else if(_box.value!!.locationId == null){
                _location.value = null
            }
        }else{
            _isRefreshing.value = false
        }
    }
    private fun fetchBox(boxId: Long?, refresh: Boolean){
        _isRefreshing.value = true
        viewModelScope.launch {
            val response = boxRepository.fetchBoxById(boxId!!)
            if (response is Resource.Success) {
                _box.value = response.value
                boxRepository.updateCachedBox(response.value)
                val locationId = response.value.locationId
                if (locationId != null) {
                    if(refresh){
                        fetchLocation(locationId)
                    }else {
                        getLocation(locationId)
                    }
                }else {
                    _location.value = null
                }
            }
            _isRefreshing.value = false
        }

    }
    private fun getLocation(locationId : Long){
        if(locationId >= 0) {
            _location.value = locationRepository.getCachedLocationById(locationId)
            if (_location.value != null) {
                return
            }else{
                fetchLocation(locationId)
            }
        }
    }
    private fun fetchLocation(locationId: Long){
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

    fun updateItemQuantity(newQuantity: Long) = viewModelScope.launch {
        val newItem = _item.value!!.copy(quantity = newQuantity, boxId = newBoxId.value)

        _updateResponse.value = repository.updateItem(newItem, false)
        if(_updateResponse.value is Resource.Success){
            repository.updateCachedItem(newItem)
            _item.value = newItem
            _newBoxId.value = -1L
        }

    }
    fun saveEnable() : Boolean{
        return newBoxId.value != -1L && newBoxId.value != item.value!!.boxId
    }
    fun saveOrUpdateItem(updatedItem: Item, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateItem(updatedItem, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedItem.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _item.value = updatedItem
                _newBoxId.value = -1L
                repository.updateCachedItem(updatedItem)
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