package com.leopardseal.inventorymanagerapp.ui.main.item


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.ItemRepository
import com.leopardseal.inventorymanagerapp.data.repositories.TagRepository
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ItemViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val tagRepository: TagRepository
) : ViewModel(){

    private val _itemResponse = MutableStateFlow<Resource<List<Item>>>(Resource.Loading)
    val itemResponse: StateFlow<Resource<List<Item>>>
        get() = _itemResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _tagResponse = MutableStateFlow<Resource<List<Tag>>>(Resource.Loading)
    val tagResponse: StateFlow<Resource<List<Tag>>>
        get() = _tagResponse

    private val _selectedTags = MutableStateFlow<List<Tag>>(emptyList())
    val selectedTags: StateFlow<List<Tag>> = _selectedTags

    private val _filteredItems = MutableStateFlow<Resource<List<Item>>>(Resource.Init)
    val filteredItems: StateFlow<Resource<List<Item>>> = _filteredItems

    private val _isFilterActive = MutableStateFlow(false)
    val isFilterActive: StateFlow<Boolean> = _isFilterActive

    private fun updateFilterState() {
        _isFilterActive.value = _selectedTags.value.isNotEmpty()
    }

    fun toggleTag(tag: Tag) {
        val current = _selectedTags.value.toMutableList()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _selectedTags.value = current
        updateFilterState()
    }

    fun filterItems() {
        val currentItems = (_itemResponse.value as? Resource.Success)?.value ?: emptyList()
        val selectedTags = _selectedTags.value

        if (selectedTags.isEmpty()) {
            _filteredItems.value = _itemResponse.value
        } else {
            val selectedIds = selectedTags.mapNotNull { it.id }.toSet()
            val filtered = currentItems.filter { item ->
                item.tags.any { it.id in selectedIds }
            }
            _filteredItems.value = Resource.Success(filtered)
        }
    }
    fun clearTags(){
        _selectedTags.value = emptyList()
        updateFilterState()
        filterItems()
    }
    init {
        if (repository.isItemListFullyCached()) {
            getItems()
        } else {
            fetchItems()
        }
    }
    fun getItems(){
        val cachedItems = repository.getCachedItems()
        if(cachedItems is Resource.Failure){
            fetchItems()
        }else{
            _itemResponse.value = cachedItems
            filterItems()
        }
    }
    fun fetchItems() = viewModelScope.launch {
        _isRefreshing.value = true
        val response = repository.getItems()
        _itemResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedItems(response.value)
            filterItems()
        }
        _isRefreshing.value = false
    }
    fun getTags(){
        _tagResponse.value = Resource.Loading
        val cachedTags = tagRepository.getCachedTags()
        if(cachedTags is Resource.Failure){
            fetchTags()
        }else{
            _tagResponse.value = cachedTags
        }
    }
    fun fetchTags() = viewModelScope.launch {
        _isRefreshing.value = true
        val response = tagRepository.getTags()
        _tagResponse.value = response

        if (response is Resource.Success) {
            tagRepository.setCachedTags(response.value)
        }
    }

}