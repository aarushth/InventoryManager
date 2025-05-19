package com.leopardseal.inventorymanagerapp.ui.main.item.expanded

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import coil.request.ImageRequest

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse


@Composable
fun ItemExpandedScreen(
    item : Items?,
    updateResponse : Resource<SaveResponse>,
    onEdit : (itemId : Long) -> Unit,
    onUpdate : (currentQuantity : Long) -> Unit,
    onUnauthorized : () -> Unit
) {
    val context = LocalContext.current

    var currentQuantity by remember { mutableStateOf(0L) }
    var originalQuantity by remember { mutableStateOf(0L) }
    var saveEnable by remember { mutableStateOf(true) }
    when (updateResponse) {
        is Resource.Success -> {
            Toast.makeText(context, "Quantity updated to $currentQuantity", Toast.LENGTH_LONG).show()
            originalQuantity = currentQuantity
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
    LaunchedEffect(item) {
        if (item != null) {
            originalQuantity = item!!.quantity
            currentQuantity = item!!.quantity
        }
    }
    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }else{
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())

            ) {
                // Image + Edit button overlay
                Box(modifier = Modifier.fillMaxWidth()) {
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data =item!!.imageUrl + "?t=${System.currentTimeMillis()}")
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.default_img)
                                error(R.drawable.default_img)
                            }).build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = item!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(347.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { item!!.id?.let { onEdit(it) } },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .height(36.dp)
                            .width(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Item Name",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name, Barcode, Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text(
                            text = item!!.name,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = item!!.barcode!!,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(0.4f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (currentQuantity > 0) currentQuantity--
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_subtract_24),
                                    contentDescription = "Decrease quantity"
                                )
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
                                item.quantity <= 0L -> "Out of stock"
                                item.quantity <= item.alert -> "Low stock"
                                else -> "In stock"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = item!!.description!!,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Box and Location info
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Box", fontWeight = FontWeight.Bold)
                        Text(
                            item!!.boxId?.toString() ?: "This is a box",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Location", fontWeight = FontWeight.Bold)
                        Text("This is a location", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

            }
            if (currentQuantity != originalQuantity && saveEnable) {
                Button(
                    onClick = {
                        saveEnable = false
                        onUpdate(currentQuantity)
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
