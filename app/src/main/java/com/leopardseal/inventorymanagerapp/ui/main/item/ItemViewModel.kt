package com.leopardseal.inventorymanagerapp.ui.main.item


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
class ItemViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel(){

    private val _itemResponse = MutableStateFlow<Resource<List<Items>>>(Resource.Loading)
    val itemResponse: StateFlow<Resource<List<Items>>>
        get() = _itemResponse

    fun getItems() = viewModelScope.launch {
        val response = repository.getItems() as Resource<List<Items>>
        _itemResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedItems(response.value)
        }
    }

}