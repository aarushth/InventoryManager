package com.leopardseal.inventorymanagerapp.ui.main.item.select

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Items
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemSelectViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val shouldRefresh by savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState(initial = false) ?: mutableStateOf(false)

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.refreshWithoutResettingSelection()
            savedStateHandle?.set("refresh", false)
        }
    }

    private val boxId: Long = savedStateHandle["box_id"] ?: -1L

    private val _items = MutableStateFlow<Resource<List<Items>>>(Resource.Loading)
    val items: StateFlow<Resource<List<Items>>>
        get() = _items

    private val _selectedItems = mutableStateListOf<Items>()
    val selectedItems: List<Items>
        get() = _selectedItems

    private var initialSelectedItems: List<Items> = emptyList()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges

    init {
        loadItems()
    }

    fun loadItems() {
        _isRefreshing.value = true
        viewModelScope.launch {
            _items.value = Resource.Loading
            val result = repository.getItems()
            _items.value = result

            if (result is Resource.Success) {
                val itemsInBox = result.value.filter { it.boxId == boxId }
                initialSelectedItems = itemsInBox
                _selectedItems.clear()
                _selectedItems.addAll(itemsInBox)
            }
            _isRefreshing.value = false
        }
    }

    fun toggleItemSelection(item: Items) {
        if (_selectedItems.contains(item)) {
            _selectedItems.remove(item)
        } else {
            _selectedItems.add(item)
        }
        _hasChanges.value = !areItemListsEqual(initialSelectedItems, _selectedItems)
    }

    private fun areItemListsEqual(list1: List<Items>, list2: List<Items>): Boolean {
        val ids1 = list1.mapNotNull { it.id }.toSet()
        val ids2 = list2.mapNotNull { it.id }.toSet()
        return ids1 == ids2
    }
    fun refresh(){
        viewModelScope.launch {
            _isRefreshing.value = true
            _items.value = Resource.Loading
            _items.value = repository.getItems()
            _isRefreshing.value = false
        }
    }
    fun isSelected(item: Items): Boolean {
        return _selectedItems.contains(item)
    }
    fun syncSelectionWithBackend(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val newlySelected = selectedItems.filterNot { it in initialSelectedItems }
            val deselected = initialSelectedItems.filterNot { it in selectedItems }

            // Add boxId to new selections
            newlySelected.forEach { item ->
                val updatedItem = item.copy(boxId = boxId)
                repository.updateItem(updatedItem, false)
            }

            deselected.forEach { item ->
                val updatedItem = item.copy(boxId = null)
                repository.updateItem(updatedItem, false)
            }
            _hasChanges.value = false
            onComplete()
        }
    }
}