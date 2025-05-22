package com.leopardseal.inventorymanagerapp.ui.main.item.expanded


import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ImageRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ItemExpandedViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val imageRepository : ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Long = savedStateHandle["item_id"] ?: -1L

    private val _item = MutableStateFlow<Items?>(repository.getCachedItemById(itemId))
    val item: StateFlow<Items?>
        get() = _item

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    init {
        _item.value = repository.getCachedItemById(itemId)
        // Then refresh from server
        if(itemId >= 0 ) {
            viewModelScope.launch {

                val response = repository.fetchItemById(itemId)
                if (response is Resource.Success) {
                    _item.value = response.value
                }

            }
        }

    }
    fun updateItemQuantity(newQuantity: Long) = viewModelScope.launch {
        val newItem = _item.value!!.copy(quantity = newQuantity)

        _updateResponse.value = repository.updateItem(newItem, false) as Resource<SaveResponse>
        _item.value = newItem
    }
    fun saveOrUpdateItem(updatedItem: Items, imageChanged : Boolean) = viewModelScope.launch {
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