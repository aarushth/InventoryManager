package com.leopardseal.inventorymanagerapp.ui.main.item.multiselect

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemMultiSelectViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    val boxId: Long = savedStateHandle["box_id"] ?: -1L

    private val _items = MutableStateFlow<Resource<List<Item>>>(Resource.Loading)
    val items: StateFlow<Resource<List<Item>>>
        get() = _items

    private val _selectedItems = mutableStateListOf<Item>()
    val selectedItems: List<Item>
        get() = _selectedItems

    private var initialSelectedItems: List<Item> = emptyList()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges

    init {
        if (repository.isItemListFullyCached()) {
            getItems(true)
        } else {
            fetchItems()
        }
    }

    fun getItems(resetSelected : Boolean) {

        _items.value = Resource.Loading
        val result = repository.getCachedItems()
        if(result is Resource.Failure){
            fetchItems()
        }else{
            _items.value = result
            if(resetSelected){
                getSelected(result as Resource.Success<List<Item>>)
            }
            _isRefreshing.value = false
        }
    }
    fun fetchItems(){
        _isRefreshing.value = true
        _hasChanges.value = false
        viewModelScope.launch {
            val result = repository.getItems()
            _items.value = result
            if (result is Resource.Success<List<Item>>) {
                getSelected(result)
            }
            _isRefreshing.value = false
        }
    }
    fun getSelected(response : Resource.Success<List<Item>>){
        val itemsInBox = (response as Resource.Success<List<Item>>).value.filter { it.boxId == boxId }
        initialSelectedItems = itemsInBox
        _selectedItems.clear()
        _selectedItems.addAll(itemsInBox)
    }
    fun toggleItemSelection(item: Item) {
        if (_selectedItems.contains(item)) {
            _selectedItems.remove(item)
        } else {
            _selectedItems.add(item)
        }
        _hasChanges.value = !areItemListsEqual(initialSelectedItems, _selectedItems)
    }

    private fun areItemListsEqual(list1: List<Item>, list2: List<Item>): Boolean {
        val ids1 = list1.mapNotNull { it.id }.toSet()
        val ids2 = list2.mapNotNull { it.id }.toSet()
        return ids1 == ids2
    }
    fun isSelected(item: Item): Boolean {
        return _selectedItems.contains(item)
    }
    fun syncSelectionWithBackend(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val newlySelected = selectedItems.filterNot { it in initialSelectedItems }
            val deselected = initialSelectedItems.filterNot { it in selectedItems }

            // Add boxId to new selections
            newlySelected.forEach { item ->
                val updatedItem = item.copy(boxId = boxId)
                val response = repository.updateItem(updatedItem, false)
                if(response is Resource.Success){
                    repository.updateCachedItem(updatedItem)
                }
            }

            deselected.forEach { item ->
                val updatedItem = item.copy(boxId = null)
                val response = repository.updateItem(updatedItem, false)
                if(response is Resource.Success){
                    repository.updateCachedItem(updatedItem)
                }
            }
            _hasChanges.value = false
            onComplete()
        }
    }
}