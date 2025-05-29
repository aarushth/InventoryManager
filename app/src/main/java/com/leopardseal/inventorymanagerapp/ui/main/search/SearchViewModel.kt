package com.leopardseal.inventorymanagerapp.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.SearchRepository
import com.leopardseal.inventorymanagerapp.data.responses.dto.SearchResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository : SearchRepository
) : ViewModel(){

    private val _searchResponse = MutableStateFlow<Resource<SearchResponse>>(Resource.Init)
    val searchResponse : StateFlow<Resource<SearchResponse>>
        get() = _searchResponse

    private val _isBarcodeSearch = MutableStateFlow<Boolean>(false)
    val isBarcodeSearch : StateFlow<Boolean>
        get() = _isBarcodeSearch

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> 
        get() = _searchQuery

//s

    private fun search(){
        if(searchQuery.value.isNotEmpty()) {
            viewModelScope.launch {
                _searchResponse.value = Resource.Loading
                _searchResponse.value = repository.search(searchQuery.value)
            }
        }
    }
    fun resetIsBarcodeSearch(){
        _isBarcodeSearch.value = false
    }
    fun searchBarcode(barcode : String){
        _searchQuery.value = barcode
        _isBarcodeSearch.value = true
        viewModelScope.launch {
            _searchResponse.value = Resource.Loading
            _searchResponse.value = repository.searchBarcode(searchQuery.value)
        }
    }
    fun clearQuery(){
        _searchQuery.value = ""
        _searchResponse.value = Resource.Init
    }
    fun onSearchChange(query: String) {
        _searchQuery.value = query
        _isBarcodeSearch.value = false
    }

    fun performSearch() {
        viewModelScope.launch {
//            _debouncedQuery.value = _searchQuery.value
            _isBarcodeSearch.value = false
            search() // your existing search logic
        }
    }

}