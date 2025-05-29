package com.leopardseal.inventorymanagerapp.ui.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.dto.SearchResponse
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxListCard
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemListCard
import com.leopardseal.inventorymanagerapp.ui.main.location.LocationHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.location.LocationListCard

@Composable
fun SearchScreen(
    viewModel : SearchViewModel = hiltViewModel(),
    navController : NavController
){

    val searchQuery by viewModel.searchQuery.collectAsState()
    
    val searchResponse by viewModel.searchResponse.collectAsState()
    val isBarcodeSearch by viewModel.isBarcodeSearch.collectAsState()

    when(val result = searchResponse){
        is Resource.Success<SearchResponse> ->{
            val searchResult = result.value
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ){
                if(isBarcodeSearch && (searchResult.itemCount + searchResult.boxCount + searchResult.locationCount) == 1){
                    viewModel.resetIsBarcodeSearch()
                    if(searchResult.itemCount == 1){
                        navController.navigate("itemExpanded/${searchResult.items[0].id}")
                    }else if(searchResult.boxCount == 1){
                        navController.navigate("boxExpanded/${searchResult.boxes[0].id}")
                    }else if(searchResult.locationCount == 1){
                        navController.navigate("locationExpanded/${searchResult.locations[0].id}")
                    }
                }
                else if((searchResult.itemCount + searchResult.boxCount + searchResult.locationCount) <= 0){
                    viewModel.resetIsBarcodeSearch()
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "no results for search query '${searchQuery}'",
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                    }
                }else{
                    viewModel.resetIsBarcodeSearch()
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
        is Resource.Loading->{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure ->{
            Text("An error occured")
        }
        else -> {}
    }
}