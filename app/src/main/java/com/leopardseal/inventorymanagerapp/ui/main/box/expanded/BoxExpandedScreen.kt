package com.leopardseal.inventorymanagerapp.ui.main.expanded

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
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse


@Composable
fun BoxExpandedScreen(
    box : Boxes?,
    onEdit : (boxId : Long) -> Unit,
) {

    if (box == null) {
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
                        ImageRequest.Builder(LocalContext.current).data(data =box!!.imageUrl + "?t=${System.currentTimeMillis()}")
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.default_img)
                                error(R.drawable.default_img)
                            }).build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = box!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(347.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { box!!.id?.let { onEdit(it) } },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .height(36.dp)
                            .width(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Box Name",
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = box!!.name,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = box!!.size!!,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = box!!.barcode!!,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )   
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Location", fontWeight = FontWeight.Bold)
                        Text("This is a location", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                // Items contained and Location info
                Row(modifier = Modifier.fillMaxWidth()) {
                    //Fill with items list horizontal
                }
            }
        }
    }
}