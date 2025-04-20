package com.leopardseal.inventorymanagerapp.ui.main.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.responses.Items
import kotlinx.coroutines.launch


class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel(){

    private val _itemResponse : MutableLiveData<Resource<List<Items>>> = MutableLiveData()
    val itemResponse: LiveData<Resource<List<Items>>>
        get() = _itemResponse

    fun getItems() = viewModelScope.launch {
        _itemResponse.value = repository.getItems() as Resource<List<Items>>
    }

}