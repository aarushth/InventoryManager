package com.leopardseal.inventorymanagerapp.ui.main.box

import android.widget.Toast
import androidx.collection.orderedScatterSetOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


import com.leopardseal.inventorymanagerapp.data.responses.Boxes

import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScreen(
        boxState : Resource<List<Boxes?>>,
        isRefreshing : Boolean,
        onRefresh: () -> Unit,
        onBoxClick: (boxId:Long) -> Unit,
        onUnauthorized: () -> Unit
        ) {
    val context = LocalContext.current

    when (boxState) {
        is Resource.Success -> {
            val boxes = boxState.value.filterNotNull()
//            delay(500)


            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = if (boxes.isEmpty()) "It looks like this org doesn't have any boxes. Click + to add an box" else "Boxes:",
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                )
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
                        items(boxes) { box ->
                            box?.let {
                                BoxCard(box = it, onClick = { it.id?.let { it1 -> onBoxClick(it1) } })
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
                Toast.makeText(context,"an error occured, please try again later", Toast.LENGTH_LONG).show()
            }
        }
        else -> {}
    }

}

@Composable
fun BoxCard(box: Boxes, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            AsyncImage(
                model = box.imageUrl + "?t=${System.currentTimeMillis()}",
                contentDescription = box.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(text = box.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = box.barcode!!, fontSize = 12.sp, color = Color.Gray)

        }
    }
}
