package com.leopardseal.inventorymanagerapp.ui.main.item.multiselect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemListCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemMultiSelectScreen(
    viewModel: ItemMultiSelectViewModel = hiltViewModel(),
    navController: NavController,
    onConfirmSelection: (List<Items>) -> Unit
){

    val itemsState by viewModel.items.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit){
        viewModel.refresh()
    }
    if(itemsState is Resource.Success) {
        val items = remember {(itemsState as Resource.Success<List<Items>>).value}
        Column(modifier = Modifier.fillMaxSize()) {

            ItemHeaderRow(hasItems = items.isNotEmpty(),
                isCardSizeToggleable = false,
                isAddable = true,
                icon = null,
                toggleCardSize = {},
                onAddClick = {navController.navigate("itemEdit/${-1L}/${false}")})

            val refreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.loadItems() }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(items) { item ->
                        ItemListCard(
                            item = item,
                            onClick = { item.id?.let { viewModel.toggleItemSelection(item) } }, selectable = true, selected = viewModel.isSelected(item))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                if(hasChanges) {
                    Button(
                        onClick = {
                            viewModel.syncSelectionWithBackend {
                                onConfirmSelection(viewModel.selectedItems)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }

        }
    }
}