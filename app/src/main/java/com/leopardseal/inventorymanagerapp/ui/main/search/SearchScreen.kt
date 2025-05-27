package com.leopardseal.inventorymanagerapp.ui.main.search

@Composable
fun SearchScreen(viewModel : SearchViewModel = hiltViewModel()){

    val query by viewModel.searchQuery.collectAsState()
    val debouncedQuery by viewModel.debouncedQuery.collectAsState()
    
    val searchResponse by viewModel.searchResponse.collectAsState()
    when(searchResponse){
        is Resource.Success ->{
            //TODO show searchContent
        }
        is Resource.Loading ->{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {}
    }
}