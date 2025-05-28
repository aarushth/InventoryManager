package com.leopardseal.inventorymanagerapp.ui.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.leopardseal.inventorymanagerapp.data.network.Resource

@Composable
fun SearchScreen(viewModel : SearchViewModel = hiltViewModel()){

    val query by viewModel.searchQuery.collectAsState()
    val debouncedQuery by viewModel.debouncedQuery.collectAsState()
    
    val searchResponse by viewModel.searchResponse.collectAsState()
    when(searchResponse){
        is Resource.Success<*> ->{
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