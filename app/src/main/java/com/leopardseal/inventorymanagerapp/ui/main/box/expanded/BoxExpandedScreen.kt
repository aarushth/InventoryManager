package com.leopardseal.inventorymanagerapp.ui.main.box.expanded

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Location
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemHeaderRow
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemListCard
import com.leopardseal.inventorymanagerapp.ui.main.location.LocationListCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxExpandedScreen(
    viewModel: BoxExpandedViewModel = hiltViewModel(),
    navController: NavController,
    onUnauthorized: () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val context = LocalContext.current

    val box by viewModel.box.collectAsState()
    val location by viewModel.location.collectAsState()
    val itemState by viewModel.itemResource.collectAsState()
    val updateResponse by viewModel.updateResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var saveEnable by remember { mutableStateOf(true) }


    LaunchedEffect(Unit){
        viewModel.getBox(false)
    }
    LaunchedEffect(box) {
        viewModel.setLocationIdIfNotPresent(box?.locationId)
    }

    val selectedLocationId = currentBackStackEntry?.savedStateHandle?.get<Long>("location_id")

    LaunchedEffect(selectedLocationId) {
        if (selectedLocationId != null && selectedLocationId != -1L) {
            viewModel.setLocationId(selectedLocationId)
        }
    }
    when (updateResponse) {
        is Resource.Success -> {
            Toast.makeText(context, "Location updated to ${location!!.name}", Toast.LENGTH_LONG).show()
            saveEnable = true
        }
        is Resource.Failure -> {
            if ((updateResponse as Resource.Failure).isNetworkError) {
                Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
            } else if ((updateResponse as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED) {
                onUnauthorized()
            } else {
                Toast.makeText(context, "An error occurred, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        is Resource.Loading-> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {}
    }

    if(box != null) {
        val refreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.getBox(true) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // Image + Edit button overlay
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = box!!.imageUrl,
                            contentDescription = box!!.name,
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
                            onClick = { box!!.id?.let { navController.navigate("boxEdit/${box!!.id}/${true}") } },
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

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = box!!.name,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                            Text(
                                text = box!!.barcode!!,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }

                        Text(
                            text = box!!.size!!.size,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp, end = 8.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(4.dp))

                    LocationChangeCard(location = location,
                        onLocationClick = { navController.navigate("locationExpanded/${location!!.id!!}") },
                        onChangeLocation = {
                            navController.navigate(
                                "locationSingleSelect/${location?.id?:-1L}"
                            )
                        }
                    )
                }
                item {
                    // ComposeItemHeaderRow logic from ItemScreen (title, toggle size, filters)
                    ItemHeaderRow(
                        hasItems = (itemState as? Resource.Success)?.value?.isNotEmpty() ?: false,
                        isCardSizeToggleable = false,
                        isAddable = true,
                        icon = null,
                        toggleCardSize = {},
                        onAddClick = { navController.navigate("itemMultiSelect/${box!!.id}") }
                    )
                }
                if (itemState is Resource.Success) {
                    val items = (itemState as Resource.Success<List<Item>>).value
                    items(items) { item ->
                        ItemListCard(
                            item = item,
                            onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
            if ( box!!.locationId != location?.id && saveEnable) {
                Button(
                    onClick = {
                        saveEnable = false
                        viewModel.updateBoxLoc()
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

@Composable
fun LocationChangeCard(location: Location?, onLocationClick: () -> Unit, onChangeLocation: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (location != null) {
            Card(shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .wrapContentSize()
            ) {
                LocationListCard(location, onClick = onLocationClick)
            }
        }
        Button(onClick = onChangeLocation ,shape = RoundedCornerShape(8.dp), modifier = Modifier
            .padding(2.dp)
            .fillMaxHeight()
            .weight(0.6f),
            contentPadding = PaddingValues(horizontal = 2.dp)) {

            Text(text = if (location == null) {"Add Location"} else {"Change Location"}, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}