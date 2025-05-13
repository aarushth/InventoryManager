package com.leopardseal.inventorymanagerapp.ui.main.item.expanded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.ui.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemExpandedViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Long = savedStateHandle["item_id"] ?: -1L

    private val _item = MutableLiveData<Items?>()
    val item: LiveData<Items?> = _item

    init {
        // Load cached item instantly (optional)
        val cached = repository.getCachedItemById(itemId)
        _item.value = cached

        // Then refresh from server
        viewModelScope.launch {
            val response = repository.fetchItemById(itemId)
            if(response is Resource.Success){
                _item.value = response.value
            }
        }
    }
    fun updateItemQuantity(newQuantity: Long) = viewModelScope.launch {
        val newItem = _item.value!!.copy(quantity = newQuantity)

        _updateResponse.value = repository.updateItem(newItem) as Resource<Unit>
        _item.value = newItem
    }

    private val _updateResponse : MutableLiveData<Resource<Unit>> = MutableLiveData()
    val updateResponse: LiveData<Resource<Unit>>
        get() = _updateResponse
}