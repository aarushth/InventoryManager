package com.leopardseal.inventorymanagerapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
//import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PullToRefreshLazyGrid(
    items:List<T>,
    content: @Composable (T) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
){

//    val pullToRefreshState = rememberPullToRefreshState()
//    Box(
//        modifier = Modifier
//            .nestedScroll(pullToRefreshState.nestedScrollConnection)
//    ){
//        Column(Modifier.align(Alignment.Center)){
//            Text(
//                text = if (items.isEmpty()) "It looks like this org doesn't have any items. Click + to add an item" else "Items:",
//                fontSize = 30.sp,
//                modifier = Modifier
//                    .padding(10.dp)
//
//            )
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(8.dp),
//            ) {
//                items(items) {
//                    content(it)
//                }
//            }
//        }
//        if(pullToRefreshState.isRefreshing){
//            LaunchedEffect(true){
//                onRefresh()
//            }
//        }
//
//
////        LaunchedEffect(isRefreshing) {
//        if (isRefreshing) {
//            pullToRefreshState.startRefresh()
//        } else {
//            // Add delay to keep spinner visible a bit longer
////                   kotlinx.coroutines.delay(800L)
//            pullToRefreshState.endRefresh()
//        }
////        }
//
//        PullToRefreshContainer(
//            state = pullToRefreshState,
//            modifier = Modifier.align(Alignment.TopCenter)
//        )
//    }
}