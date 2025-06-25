package com.leopardseal.inventorymanagerapp.ui.main.item

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.google.accompanist.flowlayout.FlowRow
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.Tag
import com.leopardseal.inventorymanagerapp.ui.Filter_alt
import com.leopardseal.inventorymanagerapp.ui.largeCardIcon
import com.leopardseal.inventorymanagerapp.ui.smallCardIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel = hiltViewModel(),
    navController: NavController,
    onUnauthorized: () -> Unit) {

    val itemState by viewModel.filteredItems.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val allTags by viewModel.tagResponse.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val isFilterActive by viewModel.isFilterActive.collectAsState()
    LaunchedEffect(Unit){
        viewModel.getItems()
        viewModel.getTags()
    }
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
                    isFilterable = true,
                    filterActive = isFilterActive,
                    icon = icon,
                    toggleCardSize = {isSmallCard = !isSmallCard},
                    onAddClick = {navController.navigate("itemEdit/${-1L}/${true}") },
                    filter = {showFilterDialog = true})
                val refreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.fetchItems() }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isSmallCard) {1} else {2}),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(items) { item ->
                            if (isSmallCard) {
                                ItemListCard(item = item, onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                            } else {
                                ItemCard(item = item, onClick = { item.id?.let { navController.navigate("itemExpanded/${item.id}") } })
                            }
                        }
                    }
                }
                if (showFilterDialog) {
                    FilterDialog(
                        allTags = (allTags as Resource.Success<List<Tag>>).value,
                        selectedTags = selectedTags,
                        onToggleTag = { viewModel.toggleTag(it) },
                        onDismiss = {
                            showFilterDialog = false
                            viewModel.filterItems()
                            if(isFilterActive){
                                isSmallCard = false
                            }
                        },
                        onClearFilters = {
                            showFilterDialog = false
                            viewModel.clearTags()}
                    )
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
fun FilterDialog(
    allTags: List<Tag>,
    selectedTags: List<Tag>,
    onToggleTag: (Tag) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Tags") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (allTags.isEmpty()) {
                    Text("No tags found.")
                } else {
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allTags.forEach { tag ->
                            val isSelected = selectedTags.contains(tag)
                            val backgroundColor = if (isSelected) Color(0xFF007BFF) else Color.Transparent
                            val borderColor = if (isSelected) Color.Transparent else Color(0xFF007BFF)
                            val textColor = if (isSelected) Color.White else Color(0xFF007BFF)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(backgroundColor)
                                    .border(2.dp, borderColor, RoundedCornerShape(20.dp))
                                    .clickable { onToggleTag(tag) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag.name,
                                    color = textColor,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        dismissButton = {
            if(selectedTags.isNotEmpty()) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear Filters")
                }
            }
        }
    )
}
@Composable
fun ItemHeaderRow(hasItems : Boolean = true, isCardSizeToggleable : Boolean, isAddable : Boolean = true, isFilterable : Boolean = false, filterActive : Boolean = false, icon:ImageVector? = null, toggleCardSize : () -> Unit = {}, onAddClick : () -> Unit = {}, filter : () -> Unit = {}){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
        Text(
            text = if (!hasItems && !filterActive){
                "No items found. Click + to add an item"
                    }else if(!hasItems && filterActive){
                     "No items found for current filter"
                    }else "Items:",
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp).weight(1f),
            maxLines = 2
        )
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.End
        )  {
            if (isFilterable) {
                IconButton(onClick = filter) {
                    Box(
                        modifier = Modifier
                            .then(
                                if (filterActive)
                                    Modifier
                                        .background(color = Color(0xFFD0FFFF), shape = CircleShape) // Light blue fill
                                        .padding(4.dp)
                                else Modifier
                            )
                    ) {
                        Icon(
                            imageVector = Filter_alt,
                            contentDescription = "Toggle Filter"
                        )
                    }
                }
            }
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
                item.quantity <= 0L -> Color.Red
                item.quantity <= item.alert -> Color.Yellow
                else -> Color.LightGray
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = SpaceBetween
            ) {
                Column {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.barcode ?: "", fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = "${item.quantity}", fontSize = 18.sp)
            }

            if (!item.tags.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                // One-line tag row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.tags.forEach { tag ->
                        Text(
                            text = tag.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(Color(0xFF2196F3), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
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
