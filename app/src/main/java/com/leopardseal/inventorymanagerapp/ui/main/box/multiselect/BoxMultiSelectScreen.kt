package com.leopardseal.inventorymanagerapp.ui.main.box.multiselect

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxListCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxMultiSelectScreen(
    viewModel: BoxMultiSelectViewModel = hiltViewModel(),
    navController: NavController,
    onConfirmSelection: (List<Box>) -> Unit
){

    val boxesState by viewModel.boxes.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit){
        viewModel.getBoxes()
    }
    if(boxesState is Resource.Success<*>) {
        val boxes = (boxesState as Resource.Success<List<Box>>).value
        Column(modifier = Modifier.fillMaxSize()) {

            BoxHeaderRow(hasBoxes = boxes.isNotEmpty(),
                isCardSizeToggleable = false,
                isAddable = true,
                icon = null,
                toggleCardSize = {},
                onAddClick = {navController.navigate("boxEdit/${-1L}/${false}")})
            
            val refreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.fetchBoxes() }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(boxes) { box ->
                        BoxListCard(
                            box = box,
                            onClick = { box.id?.let { viewModel.toggleBoxSelection(box) } }, selectable = true, selected = viewModel.isSelected(box))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                if(hasChanges) {
                    Button(
                        onClick = {
                            viewModel.syncSelectionWithBackend {
                                onConfirmSelection(viewModel.selectedBoxes)
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