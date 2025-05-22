package com.leopardseal.inventorymanagerapp.ui.main.item

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Items
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.ui.main.PullToRefreshLazyGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
        itemState : Resource<List<Items?>>,
        isRefreshing : Boolean,
        isCardSizeTogglable : Boolean,
        onRefresh: () -> Unit,
        onItemClick: (itemId:Long) -> Unit,
        onUnauthorized: () -> Unit
        ) {

    val context = LocalContext.current
    var isSmallCard by remember{mutableStateOf(false)}

    when (itemState) {
        is Resource.Success -> {
            val items = itemState.value
            Column(modifier = Modifier.fillMaxSize()) {
                Row(horizontalArrangement = SpaceBetween){
                        Text(
                            text = if (items.isEmpty()) "No items found. Click + to add an item" else "Items:",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    if(isCardSizeTogglable){
                        IconButton(onClick = {isSmallCard = !isSmallCard}) {
                            Icon(Icons.Default.Refresh, contentDescription = "Generate barcode")
                        }
                    }
                }   
                
                val refreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {onRefresh()}
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(items) { item ->
                            item?.let {
                                if(isSmallCard){
                                    ItemListCard(item = it, onClick = { it.id?.let { it1 -> onItemClick(it1) } })
                                }else{
                                    ItemCard(item = it, onClick = { it.id?.let { it1 -> onItemClick(it1) } })
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
                Toast.makeText(context,"an error occured, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        else -> {}
    }

}

@Composable
fun ItemCard(item: Items, onClick: () -> Unit) {
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
                model = item.imageUrl + "?t=${System.currentTimeMillis()}",
                contentDescription = item.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.barcode!!, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = "${item.quantity}", fontSize = 18.sp)
            }
        }
    }
}

fun ItemListCard(item: Items, onClick: () ->){
    Row(modifier = Modifier.fillMaxWidth()
    .clickable{ onClick() },
    horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(modifier = Modifier.fillMaxWidth()){
            AsyncImage(
                model = item.imageUrl + "?t=${System.currentTimeMillis()}",
                contentDescription = item.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp),
                contentScale = ContentScale.Crop
            )
            Column{
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = item.barcode!!, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()){
            Text(text = "${item.quantity}", fontSize = 18.sp)
        }
    }
}
