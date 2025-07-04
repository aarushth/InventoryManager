package com.leopardseal.inventorymanagerapp.ui.main.item.expanded

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Box
import com.leopardseal.inventorymanagerapp.data.responses.Location
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxListCard
import com.leopardseal.inventorymanagerapp.ui.main.location.LocationListCard
import com.leopardseal.inventorymanagerapp.ui.subtractIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemExpandedScreen(
    viewModel: ItemExpandedViewModel = hiltViewModel(),
    navController: NavController,
    onUnauthorized : () -> Unit,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle
    val context = LocalContext.current

    val item by viewModel.item.collectAsState()
    val box by viewModel.box.collectAsState()
    val location by viewModel.location.collectAsState()
    val updateResponse by viewModel.updateResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()


    val selectedBoxId = currentBackStackEntry?.savedStateHandle?.get<Long>("box_id")

    LaunchedEffect(selectedBoxId) {
        if (selectedBoxId != null && selectedBoxId != -1L) {
            viewModel.setBoxIdIfNotPresent(selectedBoxId)
        }
    }
    LaunchedEffect(Unit){
        viewModel.getItem()
    }

    var currentQuantity by rememberSaveable { mutableLongStateOf(savedStateHandle?.get<Long>("currentQuantity") ?: (item?.quantity?: 0L)) }
    var originalQuantity by rememberSaveable { mutableLongStateOf(savedStateHandle?.get<Long>("originalQuantity") ?: (item?.quantity?: 0L)) }
    LaunchedEffect(item) {
        currentQuantity = item?.quantity?:0L
        originalQuantity = item?.quantity?:0L
    }

    LaunchedEffect(currentQuantity) { savedStateHandle?.set("currentQuantity", currentQuantity) }
    LaunchedEffect(originalQuantity) { savedStateHandle?.set("originalQuantity", originalQuantity) }
    val updateItem : () -> Unit = {viewModel.updateItemQuantity(currentQuantity)}

    when (updateResponse) {
        is Resource.Success -> {
            Toast.makeText(context, "Changes Saved", Toast.LENGTH_LONG).show()
            originalQuantity = currentQuantity
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
    val refreshState = rememberPullToRefreshState()
    if(item!= null) {
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchItem(true) }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())

            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = item!!.imageUrl,
                        contentDescription = item!!.name,
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
                        onClick = { item!!.id?.let { navController.navigate("itemEdit/${item!!.id}/${true}") } },
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

                // Name, Barcode, Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text(
                            text = item!!.name,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                        )
                        Text(
                            text = item!!.barcode!!,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(0.4f).padding(end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (currentQuantity > 0) currentQuantity--
                            }) {
                                Icon(subtractIcon, contentDescription = "Decrease quantity")
                            }
                            Text(
                                text = currentQuantity.toString(),
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { currentQuantity++ }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase quantity"
                                )
                            }
                        }
                        Text(
                            text = when {
                                item!!.quantity <= 0L -> "Out of stock"
                                item!!.quantity <= item!!.alert -> "Low stock"
                                else -> "In stock"
                            }
                        )
                    }
                }
                // Description
                Text(
                    text = item!!.description!!,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Transparent)
                            .border(
                                width = 2.dp,
                                color = Color(0xFF007BFF),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { navController.navigate("tagsMultiSelect/${item!!.id}")}
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Tags",
                                Modifier,
                                Color(0xFF007BFF)
                            )
                            Text(
                                text = if (item?.tags?.isEmpty() == true) "Add Tags" else "Edit Tags",
                                color = Color(0xFF007BFF),
                                fontSize = 18.sp
                            )
                        }
                    }
                    item?.tags?.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF007BFF))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag.name,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                //box
                Spacer(modifier = Modifier.height(12.dp))
                BoxChangeCard(box = box,
                    onBoxClick = { navController.navigate("boxExpanded/${box!!.id!!}") },
                    onChangeBox = {navController.navigate("boxSingleSelect/${if(box!=null){box!!.id}else{-1L}}")}
                )
                //location
                if(box != null) {
                    LocationCard(location, onLocationClick = {navController.navigate("locationExpanded/${location!!.id}")})
                }

                Spacer(modifier = Modifier.height(72.dp))

            }
            if ((currentQuantity != originalQuantity || item!!.boxId != box?.id) && viewModel.saveEnable()) {
                Button(
                    onClick = updateItem,
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
fun BoxChangeCard(box: Box?, onBoxClick: () -> Unit, onChangeBox: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (box != null) {
            Card(shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .wrapContentSize()
            ) {
                BoxListCard(box, onClick = { onBoxClick() })
            }
        }
        Button(onClick = { onChangeBox() },shape = RoundedCornerShape(8.dp), modifier = Modifier
            .padding(2.dp)
            .fillMaxHeight()
            .weight(0.6f)) {
            Text(if (box == null) {"Add To Box"} else {"Change Box"}, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LocationCard(location: Location?, onLocationClick: () -> Unit){
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
                LocationListCard(location, onClick = { onLocationClick() })
            }
        }else{
            Text("box has no location set")
        }
    }
}
