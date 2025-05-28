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

    private val _searchResponse = MutableStateFlow<Resource<SearchResponse>>(Resource.Loading)
    val searchResponse : StateFlow<Resource<SearchResponse>>
        get() = _searchResponse

    

    private fun search(){
        viewModelScope.launch {
            _searchResponse.value = repository.search(debouncedQuery.value)
        }
    }

    
    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> 
        get() = _searchQuery

    private val _debouncedQuery = MutableStateFlow("")
    val debouncedQuery: StateFlow<String> 
        get() = _debouncedQuery

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // wait 300ms after last input
                .distinctUntilChanged()
                .collectLatest { query ->
                    _debouncedQuery.value = query
                    search()
                }
        }
    }
}