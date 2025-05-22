
import android.net.Uri
import android.widget.Toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.lifecycle.SavedStateHandle

import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Locations
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse

import java.io.File
import java.util.*
import androidx.compose.runtime.getValue

import androidx.navigation.NavBackStackEntry


@Composable
fun LocationEditScreen(
    location: Locations?, // null = new location
    updateResponse: Resource<SaveResponse>,
    uploadImgResponse: Resource<Unit>,
    currentBackStackEntry: NavBackStackEntry?,
    onImageCapture: ()->Unit,
    orgId : Long,
    onSave: (Locations, Boolean) -> Unit,
    onSaveComplete: (imageUri : File?) -> Unit,
    onUnauthorized: () -> Unit,
    onImageSaved: () -> Unit,
    onScanBarcodeClick: () -> Unit
) {
    val context = LocalContext.current

    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle

    // Input state with initial values depending on whether location is null or not
    var name by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("name") ?: location?.name.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("description") ?: location?.description.orEmpty()) }
    var barcode by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("barcode") ?: location?.barcode.orEmpty()) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(savedStateHandle?.get<Uri>("imageUri")) }
    var imageFile by rememberSaveable  { mutableStateOf<File?>(savedStateHandle?.get<File>("imageFile")) }

    val isNameValid = name.isNotBlank()

    val isSaveEnabled = if (location == null) {
        isNameValid 
    } else {
        name != location.name ||
                description != location.description ||
                barcode != location.barcode.orEmpty() ||
                imageFile != null
    }

    LaunchedEffect(name) { savedStateHandle?.set("name", name) }
    LaunchedEffect(description) { savedStateHandle?.set("description", description) }
    LaunchedEffect(barcode) { savedStateHandle?.set("barcode", barcode)}
    LaunchedEffect(imageUri) { savedStateHandle?.set("imageUri", imageUri) }
    LaunchedEffect(imageFile) { savedStateHandle?.set("imageFile", imageFile) }

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
            location!!.id?.let {
                onSaveComplete(imageFile)
                savedStateHandle?.set("barcode", "")
            }
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
            onImageSaved()

        }
        is Resource.Failure -> {
            if (uploadImgResponse.isNetworkError) {
                Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
            } else if (uploadImgResponse.errorCode == HttpStatus.SC_UNAUTHORIZED) {
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
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = if (location != null) {
                                location.imageUrl + "?t=${System.currentTimeMillis()}"
                            } else {
                                R.drawable.default_img
                            }
                        )
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.default_img)
                                error(R.drawable.default_img)
                            }).build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = name,
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(16.dp))

                Button(onClick = { onImageCapture() }) {
                    Icon(Icons.Default.Phone, contentDescription = "Take Photo")
                    Spacer(Modifier.width(8.dp))
                    Text("Take Image")
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

                Button(onClick = { onScanBarcodeClick() }) {
                    Icon(Icons.Default.Phone, contentDescription = "Scan Barcode")
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Scan Barcode",
                        textAlign = TextAlign.Center
                    )
                }

            }

            Spacer(modifier = Modifier.height(40.dp))
        }
        if(isSaveEnabled) {
            Button(
                onClick = {
                    val newLocation = location?.copy(
                        name = name.trim(),
                        description = description.trim(),
                        barcode = barcode.trim(),
                    ) ?: Locations( 
                        name = name.trim(),
                        orgId = orgId, 
                        barcode = barcode.trim(),
                        description = description.trim(),
                        boxId = null,
                        imageUrl = null
                    )
                    onSave(newLocation, (imageFile != null))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(if (location == null) "Create Location" else "Save Changes")
            }
        }
    }
}