package com.leopardseal.inventorymanagerapp.ui.main.box.singleselect


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.ui.largeCardIcon
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxCard
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxListCard
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxViewModel
import com.leopardseal.inventorymanagerapp.ui.smallCardIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxSingleSelectScreen(
    viewModel: BoxViewModel = hiltViewModel(),
    navController: NavController,
    boxSelected : Long = -1L,
    onUnauthorized: () -> Unit,
    ) {
    val boxState by viewModel.boxResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    var isSmallCard by rememberSaveable{mutableStateOf(true)}
    LaunchedEffect(Unit){
        viewModel.getBoxes()
    }
    when (boxState) {
        is Resource.Success<*> -> {
            val boxes = (boxState as Resource.Success<List<Box>>).value.filterNotNull()
            Column(modifier = Modifier.fillMaxSize()) {


                val icon = if (isSmallCard) largeCardIcon else smallCardIcon
                BoxHeaderRow(hasBoxes = boxes.isNotEmpty(),
                    isCardSizeToggleable = true,
                    isAddable = true,
                    icon = icon,
                    toggleCardSize = {isSmallCard = !isSmallCard},
                    onAddClick = {navController.navigate("boxEdit/${-1L}/${false}")})

                val refreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.getBoxes()}
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isSmallCard) {1} else {2}),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(boxes) { box ->
                            if(isSmallCard){
                                BoxListCard(
                                    box = box, 
                                    onClick = { 
                                        box.id?.let {  
                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("box_id", it)
                                            navController.popBackStack()
                                        } 
                                    }, 
                                    selected = box.id!! == boxSelected
                                )
                            }else {
                                BoxCard(
                                    box = box, 
                                    onClick = { 
                                            box.id?.let {  
                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("box_id", it)
                                            navController.popBackStack()
                                        }  
                                    }, 
                                    selected = box.id!! == boxSelected
                                )
                            }
                        }
                    }
                }

            }
        }

        is Resource.Failure -> {
            if((boxState as Resource.Failure).isNetworkError) {
                Toast.makeText(context,"please check your internet and try again", Toast.LENGTH_LONG).show()
            }else if((boxState as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED){
                onUnauthorized()
            }else{
                Toast.makeText(context,"an error occurred, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        else -> {}
    }

}
