package com.leopardseal.inventorymanagerapp.ui.main.location.expanded

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Location
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import com.leopardseal.inventorymanagerapp.ui.barcodeIcon
import com.leopardseal.inventorymanagerapp.ui.cameraIcon
import java.io.File
import java.util.UUID


@Composable
fun LocationEditScreen(
    viewModel: LocationExpandedViewModel = hiltViewModel(),
    navController: NavController,
    innerPaddingValues : PaddingValues,
    orgId : Long,
    onContinue : (locationId : Long) -> Unit,
    onUnauthorized: () -> Unit
) {
    val location by viewModel.location.collectAsState()
    val updateResponse by viewModel.updateResponse.collectAsState()
    val uploadImgResponse by viewModel.uploadResult.collectAsState()

    val context = LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var barcode by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var imageFile by rememberSaveable { mutableStateOf<File?>(null) }

    val initialized = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit){
        if(!initialized.value){
            viewModel.getLocation()
        }
    }
    LaunchedEffect(location) {
        if (location != null && !initialized.value) {
            name = location!!.name.orEmpty()
            description = location!!.description.orEmpty()
            barcode = location!!.barcode.orEmpty()
            initialized.value = true
        }
    }
    val isNameValid = name.isNotBlank()

    val isSaveEnabled = if (location == null) {
        isNameValid 
    } else {
        name != location!!.name ||
                description != location!!.description ||
                barcode != location!!.barcode.orEmpty() ||
                imageFile != null
    }
    if(savedStateHandle != null){
        val barcodeFlow = savedStateHandle.getStateFlow("barcode", "")
        val scannedBarcode by barcodeFlow.collectAsState()
        LaunchedEffect(scannedBarcode) {
            if (scannedBarcode.isNotBlank()) {
                barcode = scannedBarcode
                savedStateHandle["barcode"] = ""
            }
        }
        val imageUriFlow = savedStateHandle.getStateFlow<Uri?>("imageUri", null)
        val imageUriTaken by imageUriFlow.collectAsState()
        LaunchedEffect(imageUriTaken) {
            if (imageUriTaken != null) {
                imageUri = imageUriTaken
                savedStateHandle["imageUri"] = null
            }
        }
        val imageFileFlow = savedStateHandle.getStateFlow<File?>("imageFile", null)
        val imageFileTaken by imageFileFlow.collectAsState()
        LaunchedEffect(imageFileTaken) {
            if (imageFileTaken != null) {
                imageFile = imageFileTaken
                savedStateHandle["imageFile"] = null
            }
        }
    }

    when (updateResponse) {
        is Resource.Success<SaveResponse> -> {
            Toast.makeText(context, "Location saved", Toast.LENGTH_LONG).show()
            if((updateResponse as Resource.Success).value.imageUrl == null){
                viewModel.resetUpdateResponse()
                onContinue(location!!.id!!)
            }else{
                viewModel.uploadImage(
                    (updateResponse as Resource.Success).value.imageUrl!!,
                    imageFile!!
                )
            }
            savedStateHandle?.set("barcode", "")

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
    when (uploadImgResponse) {
        is Resource.Success<Unit> -> {
            Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show()
            viewModel.resetUploadFlag()
            onContinue(location!!.id!!)

        }
        is Resource.Failure -> {
            if ((uploadImgResponse as Resource.Failure).isNetworkError) {
                Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
            } else if ((uploadImgResponse as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED) {
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
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {


                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    AsyncImage(
                        model = location?.imageUrl,
                        contentDescription = location?.name,
                        placeholder = painterResource(R.drawable.default_img),
                        error = painterResource(R.drawable.default_img),
                        fallback = painterResource(R.drawable.default_img),
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Button(onClick = { navController.navigate("camera") }) {
                        Icon(cameraIcon, contentDescription = "Take Photo")
                        Spacer(Modifier.width(8.dp))
                        Text("Take Image")
                    }
                    Button(onClick = { navController.navigate("photoPicker") }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Upload Photo From Device")
                        Spacer(Modifier.width(5.dp))
                        Text(text = "Upload From Device", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }

            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Location Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode") },
                    modifier = Modifier.width(180.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            barcode = UUID.randomUUID().toString().take(12)
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Generate barcode")
                        }
                    }
                )
                Spacer(Modifier.width(16.dp))

                Button(onClick = { navController.navigate("barcode") }) {
                    Icon(barcodeIcon, contentDescription = "Scan Barcode")
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Scan Barcode",
                        textAlign = TextAlign.Center
                    )
                }

            }

            Spacer(modifier = Modifier.height(72.dp))
        }
        if(isSaveEnabled) {
            Button(
                onClick = {
                    val newLocation = location?.copy(
                        name = name.trim(),
                        description = description.trim(),
                        barcode = barcode.trim(),
                    ) ?: Location( 
                        name = name.trim(),
                        orgId = orgId, 
                        barcode = barcode.trim(),
                        description = description.trim(),
                        imageUrl = null
                    )
                    viewModel.saveOrUpdateLocation(newLocation, (imageFile != null))

                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .consumeWindowInsets(innerPaddingValues)
                    .imePadding()
                    .fillMaxWidth(),
            ) {
                Text(if (location == null) "Create Location" else "Save Changes")
            }
        }
    }
}