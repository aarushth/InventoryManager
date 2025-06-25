package com.leopardseal.inventorymanagerapp.ui.main.item.tag



import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
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
class TagSelectViewModel @Inject constructor(
    private val repository: TagRepository,
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    val itemId: Long = savedStateHandle["item_id"] ?: -1L
    var item : Item? = null
    private val _tagResponse = MutableStateFlow<Resource<List<Tag>>>(Resource.Loading)
    val tagResponse: StateFlow<Resource<List<Tag>>>
        get() = _tagResponse

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private var initialSelectedTags: List<Tag> = emptyList()

    private val _selectedTags = mutableStateListOf<Tag>()
    val selectedTags: List<Tag>
        get() = _selectedTags

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges

    init {
        getTags()
    }
    fun getTags(){
        _tagResponse.value = Resource.Loading
        val cachedTags = repository.getCachedTags()
        if(cachedTags is Resource.Failure){
            fetchTags()
        }else{
            _tagResponse.value = cachedTags
            checkTagSelection((cachedTags as Resource.Success<List<Tag>>).value)
        }
    }
    fun fetchTags() = viewModelScope.launch {
        _isRefreshing.value = true
        val response = repository.getTags()
        _tagResponse.value = response

        if (response is Resource.Success) {
            repository.setCachedTags(response.value)
            checkTagSelection(response.value)
        }

    }
    fun checkTagSelection(tags : List<Tag>){
        item = itemRepository.getCachedItemById(itemId);
        if(item == null){
            viewModelScope.launch {
                val newItem = itemRepository.fetchItemById(itemId)
                _selectedTags.clear()
                _selectedTags.addAll(initialSelectedTags)
                _isRefreshing.value = false
            }
        }else{
            initialSelectedTags = item!!.tags
            _selectedTags.clear()
            _selectedTags.addAll(initialSelectedTags)
            _isRefreshing.value = false
        }
    }
    fun addTag(tag : String){
        viewModelScope.launch {
            val response = repository.addTag(tag)
            if(response is Resource.Success){
                repository.updateCachedTag(response.value)
                _tagResponse.value = repository.getCachedTags()
            }
        }
    }
    fun toggleTagSelection(tag: Tag){
        if (_selectedTags.contains(tag)) {
            _selectedTags.remove(tag)
        } else {
            _selectedTags.add(tag)
        }
        _hasChanges.value = !areItemListsEqual(initialSelectedTags, _selectedTags)
    }
    private fun areItemListsEqual(list1: List<Tag>, list2: List<Tag>): Boolean {
        val ids1 = list1.mapNotNull { it.id }.toSet()
        val ids2 = list2.mapNotNull { it.id }.toSet()
        return ids1 == ids2
    }
    fun isSelected(tag: Tag): Boolean {
        return _selectedTags.contains(tag)
    }

    fun syncSelectionWithBackend(onComplete: () -> Unit = {}){
        viewModelScope.launch {

            if (item != null) {
                val newItem = item!!.copy(tags = selectedTags)
                val response = itemRepository.updateItem(newItem, false)
                if(response is Resource.Success){
                    itemRepository.updateCachedItem(newItem)
                    onComplete()
                }
            }
        }
    }
}