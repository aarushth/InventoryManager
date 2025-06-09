package com.leopardseal.inventorymanagerapp.ui.main.box.multiselect

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.BoxRepository
import com.leopardseal.inventorymanagerapp.data.responses.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxMultiSelectViewModel @Inject constructor(
    private val repository: BoxRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: Long = savedStateHandle["location_id"] ?: -1L

    private val _boxes = MutableStateFlow<Resource<List<Box>>>(Resource.Loading)
    val boxes: StateFlow<Resource<List<Box>>>
        get() = _boxes

    private val _selectedBoxes = mutableStateListOf<Box>()
    val selectedBoxes: List<Box>
        get() = _selectedBoxes

    private var initialSelectedBoxes: List<Box> = emptyList()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges

    init {
        loadBoxes()
    }
    fun refresh(){
        _isRefreshing.value = true
        viewModelScope.launch {
            _boxes.value = repository.getBoxes()
            _isRefreshing.value = false
        }
    }
    fun loadBoxes() {
        _isRefreshing.value = true
        viewModelScope.launch {
            _boxes.value = Resource.Loading
            val result = repository.getBoxes()
            _boxes.value = result

            if (result is Resource.Success) {
                val boxesInLocation = result.value.filter { it.locationId == locationId }
                initialSelectedBoxes = boxesInLocation
                _selectedBoxes.clear()
                _selectedBoxes.addAll(boxesInLocation)
            }
            _isRefreshing.value = false
        }
    }

    fun toggleBoxSelection(box: Box) {
        if (_selectedBoxes.contains(box)) {
            _selectedBoxes.remove(box)
        } else {
            _selectedBoxes.add(box)
        }
        _hasChanges.value = !areBoxListsEqual(initialSelectedBoxes, _selectedBoxes)
    }

    private fun areBoxListsEqual(list1: List<Box>, list2: List<Box>): Boolean {
        val ids1 = list1.mapNotNull { it.id }.toSet()
        val ids2 = list2.mapNotNull { it.id }.toSet()
        return ids1 == ids2
    }
    fun isSelected(box: Box): Boolean {
        return _selectedBoxes.contains(box)
    }
    fun syncSelectionWithBackend(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val newlySelected = selectedBoxes.filterNot { it in initialSelectedBoxes }
            val deselected = initialSelectedBoxes.filterNot { it in selectedBoxes }

            // Add locationId to new selections
            newlySelected.forEach { box ->
                val updatedBox = box.copy(locationId = locationId)
                repository.updateBox(updatedBox, false)
            }

            deselected.forEach { box ->
                val updatedBox = box.copy(locationId = null)
                repository.updateBox(updatedBox, false)
            }
            _hasChanges.value = false
            onComplete()
        }
    }
}