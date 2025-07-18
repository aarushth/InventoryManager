package com.leopardseal.inventorymanagerapp.ui.main.box

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
class BoxViewModel @Inject constructor(
    private val repository: BoxRepository
) : ViewModel(){

    private val _boxResponse = MutableStateFlow<Resource<List<Box>>>(Resource.Loading)
    val boxResponse: StateFlow<Resource<List<Box>>>
        get() = _boxResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing
    init {
        if (repository.isBoxListFullyCached()) {
            getBoxes()
        } else {
            fetchBoxes()
        }
    }
    fun getBoxes(){
        val cachedBoxes = repository.getCachedBoxes()
        if(cachedBoxes is Resource.Failure){
            fetchBoxes()
        }else{
            _boxResponse.value = cachedBoxes
        }
    }
    fun fetchBoxes() = viewModelScope.launch {
        _isRefreshing.value = true
        val response = repository.getBoxes() as Resource<List<Box>>
        _boxResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedBoxes(response.value)
        }
        _isRefreshing.value = false
    }

}