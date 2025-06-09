package com.leopardseal.inventorymanagerapp.ui.main.item

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.ui.largeCardIcon
import com.leopardseal.inventorymanagerapp.ui.smallCardIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    navController: NavController,
    onUnauthorized: () -> Unit) {

    val itemState by viewModel.itemResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    var isSmallCard by remember{mutableStateOf(true)}
    when (itemState) {
        is Resource.Success -> {
            val items = (itemState as Resource.Success<List<Item>>).value
            Column(modifier = Modifier.fillMaxSize()) {
                val icon = if (isSmallCard) largeCardIcon else smallCardIcon
                ItemHeaderRow(hasItems = items.isNotEmpty(),
                    isCardSizeToggleable = true,
                    isAddable = true,
                    icon = icon,
                    toggleCardSize = {isSmallCard = !isSmallCard},
                    onAddClick = {navController.navigate("itemEdit/${-1L}/${true}") })
                val refreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.getItems() }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isSmallCard) {1} else {2}),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(items) { item ->
                            if (isSmallCard) {
                                ItemListCard(item = item, onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                            } else {
                                ItemCard(
                                    item = item, onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                            }
                        }
                    }
                }
            }
        }
        is Resource.Failure -> {
            if((itemState as Resource.Failure).isNetworkError) {
                Toast.makeText(context,"please check your internet and try again", Toast.LENGTH_LONG).show()
            }else if((itemState as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED){
                onUnauthorized()
            }else{
                Toast.makeText(context,"an error occurred, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        else -> {}
    }

}

@Composable
fun ItemHeaderRow(hasItems : Boolean = true, isCardSizeToggleable : Boolean, isAddable : Boolean = true, icon:ImageVector? = null, toggleCardSize : () -> Unit = {}, onAddClick : () -> Unit = {}){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
        Text(
            text = if (!hasItems) "No items found. Click + to add an item" else "Items:",
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp).weight(1f),
            maxLines = 2
        )
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.End
        )  {
            if (isCardSizeToggleable) {
                IconButton(onClick = toggleCardSize) {
                    if (icon != null) {
                        Icon(icon, contentDescription = "Toggle Card Size")
                    }
                }
            }
            if(isAddable) {
                IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add new Item")
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                item.quantity <= 0L -> Color.Red          // Out of stock
                item.quantity <= item.alert -> Color.Yellow // Low stock
                else -> Color.LightGray                   // Normal
            }
        )
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                fallback = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
                Column {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.barcode!!, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = "${item.quantity}", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ItemListCard(item: Item, onClick: () -> Unit, selectable : Boolean = false, selected : Boolean = false){
    Column(modifier = Modifier.background(when {
        item.quantity <= 0L -> Color.Red          // Out of stock
        item.quantity <= item.alert -> Color.Yellow // Low stock
        else -> Color.White                   // Normal
    })) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(4.dp)
                .clickable { onClick() },

            horizontalArrangement = SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Row {
                if(selectable){
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onClick() }
                    )
                }
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    placeholder = painterResource(R.drawable.default_img),
                    error = painterResource(R.drawable.default_img),
                    fallback = painterResource(R.drawable.default_img),
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.barcode!!, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when {
                        item.quantity <= 0L -> "Out of stock"
                        item.quantity <= item.alert -> "Low stock"
                        else -> "In stock"
                    },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "${item.quantity}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}
