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
            val searchResult = (searchResponse as Resource.Success<searchResponse>).value
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ){
                if((searchResult.itemCount + searchResult.boxCount + searchResult.locationCount) <= 0){
                    Text(modifier = Modifier.fillMaxWidth(), text = "no results for search query '${debouncedQuery.value}'")
                }else{
                    if(searchResult.itemCount > 0){
                        item{
                            ItemHeaderRow(isCardSizeToggleable = false, isAddable = false)
                        }
                        items(searchResult.items){ item ->
                            ItemListCard(
                                item = item,
                                onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                        }
                    }
                    if(searchResult.boxCount > 0){
                        item{
                            BoxHeaderRow(isCardSizeToggleable = false, isAddable = false)
                        }
                        items(searchResult.boxes){ box ->
                            BoxListCard(
                                box = box,
                                onClick = { box.id?.let { navController.navigate("boxExpanded/${box.id}") } })
                        }
                    }
                    if(searchResult.locationCount > 0){
                        item{
                            LocationHeaderRow(isCardSizeToggleable = false, isAddable = false)
                        }
                        items(searchResult.locations){ location ->
                            LocationListCard(
                                location = location,
                                onClick = { location.id?.let { navController.navigate("locationExpanded/${location.id}") } })
                        }
                    }
                }
                
            }
        }
        is Resource.Loading ->{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {}
    }
}