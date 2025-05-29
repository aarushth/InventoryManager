package com.leopardseal.inventorymanagerapp.ui.main.location


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Locations
import com.leopardseal.inventorymanagerapp.ui.largeCardIcon
import com.leopardseal.inventorymanagerapp.ui.smallCardIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSingleSelectScreen(
    viewModel: LocationViewModel = hiltViewModel(),
    navController: NavController,
    locationSelected : Long = -1L,
    onUnauthorized: () -> Unit
) {
    val locationState by viewModel.locationResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    var isSmallCard by remember{ mutableStateOf(true) }
    LaunchedEffect(Unit){
        viewModel.getLocations()
    }
    when (locationState) {
        is Resource.Success -> {
            val locations = (locationState as Resource.Success<List<Locations>>).value
            Column(modifier = Modifier.fillMaxSize()) {
                val icon = if (isSmallCard) largeCardIcon else smallCardIcon
                LocationHeaderRow(hasLocations = locations.isNotEmpty(),
                    isCardSizeToggleable = true,
                    isAddable = true,
                    icon = icon,
                    toggleCardSize = {isSmallCard = !isSmallCard},
                    onAddClick = { navController.navigate("locationEdit/${-1L}/${false}")}
                )
                val refreshState = rememberPullToRefreshState()

                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.getLocations()}
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isSmallCard) {1} else {2}),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(locations) { location ->
                            if(isSmallCard){
                                LocationListCard(
                                    location = location, 
                                    onClick = { location.id?.let()
                                        { 
                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("location_id", it)
                                            navController.popBackStack() 
                                        } 
                                    }, 
                                    location.id!! == locationSelected
                                )
                            }else {
                                LocationCard(
                                    location = location, 
                                    onClick = { 
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("location_id", location.id)
                                        navController.popBackStack() 
                                        }, 
                                    location.id!! == locationSelected
                                )
                            }
                        }
                    }
                }
            }
        }
        is Resource.Failure -> {
            if((locationState as Resource.Failure).isNetworkError) {
                Toast.makeText(context,"please check your internet and try again", Toast.LENGTH_LONG).show()
            }else if((locationState as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED){
                onUnauthorized()
            }else{
                Toast.makeText(context,"an error occured, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        else -> {}
    }

}

