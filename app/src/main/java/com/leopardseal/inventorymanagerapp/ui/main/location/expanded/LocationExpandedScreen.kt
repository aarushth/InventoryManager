package com.leopardseal.inventorymanagerapp.ui.main.location.expanded

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxListCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationExpandedScreen(
    viewModel: LocationExpandedViewModel = hiltViewModel(),
    navController: NavController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val location by viewModel.location.collectAsState()
    val boxState by viewModel.boxResource.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(savedStateHandle) {
        val refresh = savedStateHandle?.getLiveData<Boolean>("refresh")
        refresh?.observeForever { shouldRefresh ->
            if (shouldRefresh == true) {
                viewModel.getLocation()
                savedStateHandle["refresh"] = false
            }
        }
    }

    if(location != null){
        val refreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = {viewModel.getLocation()}
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)

            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = location!!.imageUrl,
                            contentDescription = location!!.name,
                            placeholder = painterResource(R.drawable.default_img),
                            error = painterResource(R.drawable.default_img),
                            fallback = painterResource(R.drawable.default_img),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = { location!!.id?.let { navController.navigate("locationEdit/${location!!.id}/${true}") } },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .height(36.dp)
                                .width(36.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White, shape = CircleShape)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Item Name",
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location!!.name,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                    Text(
                        text = location!!.barcode!!,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )

                    Text(
                        text = location!!.description!!,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                    )
                }
                item{

                    BoxHeaderRow(hasBoxes = (boxState as? Resource.Success)?.value?.isNotEmpty() ?: false,
                        isCardSizeToggleable = false,
                        isAddable = true,
                        toggleCardSize = {},
                        icon = null,
                        onAddClick = { navController.navigate("boxMultiSelect/${location!!.id}") }
                    )
                }
                if (boxState is Resource.Success) {
                    val boxes = (boxState as Resource.Success<List<Boxes>>).value
                    items(boxes) { box ->
                        BoxListCard(
                            box = box,
                            onClick = { box.id?.let { navController.navigate("boxExpanded/${box.id}") } })
                    }
                }
            }
        }
    }
}