package com.leopardseal.inventorymanagerapp.ui.main.box

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

@HiltViewModel
class BoxViewModel @Inject constructor(
    private val repository: BoxRepository
) : ViewModel(){

    private val _boxResponse = MutableStateFlow<Resource<List<Boxes>>>(Resource.Loading)
    val boxResponse: StateFlow<Resource<List<Boxes>>>
        get() = _boxResponse

    fun getBoxes() = viewModelScope.launch {
        val response = repository.getBoxes() as Resource<List<Boxes>>
        _boxResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedBoxes(response.value)
        }
    }

}