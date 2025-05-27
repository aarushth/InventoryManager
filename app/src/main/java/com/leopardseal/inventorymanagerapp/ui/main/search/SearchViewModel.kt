package com.leopardseal.inventorymanagerapp.ui.main.search

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository : SearchRepository
) : ViewModel(){

    private val _searchResponse = MutableStateFlow<SearchResponse>(Resource.Loading)
    val searchResponse : StateFlow<SearchResponse>
        get() = _searchResponse

    

    fun search(){
        _searchResponse.value = repository.search(debouncedQuery.value)
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