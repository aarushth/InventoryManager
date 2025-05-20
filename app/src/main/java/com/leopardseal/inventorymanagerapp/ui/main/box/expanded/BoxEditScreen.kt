import android.graphics.Bitmap
import android.graphics.Paint.Align
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import kotlinx.coroutines.runBlocking
import java.util.*



@Composable
fun BoxEditScreen(
    box: Boxes?, // null = new box
    updateResponse: Resource<SaveResponse>,
    uploadImgResponse: Resource<Unit>,
    parentEntry: NavBackStackEntry,
    orgId : Long,
    onSave: (Boxes, Boolean) -> Unit,
    onSaveComplete: (imageFile : File?) -> Unit,
    onUnauthorized: () -> Unit,
    onImageSaved: () -> Unit,
    onScanBarcodeClick: () -> Unit
) {
    val context = LocalContext.current

    // Input state with initial values depending on whether box is null or not
    var name by remember { mutableStateOf(box?.name.orEmpty()) }
    var description by remember { mutableStateOf(box?.description.orEmpty()) }
    var quantity by remember { mutableStateOf(box?.quantity?.toString().orEmpty()) }
    var alertQuantity by remember { mutableStateOf(box?.alert?.toString().orEmpty()) }
    var barcode by remember { mutableStateOf(box?.barcode.orEmpty()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var saveEnable by remember{ mutableStateOf(true) }


    val savedStateHandle = parentEntry.savedStateHandle

    val barcodeFlow = parentEntry.savedStateHandle.getStateFlow("barcode", "")
    val scannedBarcode by barcodeFlow.collectAsState()
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotBlank()) {
            barcode = scannedBarcode
            parentEntry.savedStateHandle["barcode"] = "" // clear after handling
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            // Crop to square
            val size = minOf(it.width, it.height)
            val xOffset = (it.width - size) / 2
            val yOffset = (it.height - size) / 2
            val squareBitmap = Bitmap.createBitmap(it, xOffset, yOffset, size, size)

            imageFile = saveImageTemporarily(context, bitmap)
            imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        }
    }
    when (updateResponse) {
        is Resource.Success<SaveResponse> -> {
            Toast.makeText(context, "Box saved", Toast.LENGTH_LONG).show()
            box!!.id?.let { onSaveComplete(imageFile) }
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
            saveEnable = true
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
                    ImageRequest.Builder(LocalContext.current).data(data =  if(box != null){box!!.imageUrl + "?t=${System.currentTimeMillis()}"} else{R.drawable.default_img})
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

            Button(onClick = { cameraLauncher.launch() }) {
                Icon(Icons.Default.Phone, contentDescription = "Take Photo")
                Spacer(Modifier.width(8.dp))
                Text("Take Image")
            }

        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Box Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { if (it.all { c -> c.isDigit() }) quantity = it },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = alertQuantity,
            onValueChange = { if (it.all { c -> c.isDigit() }) alertQuantity = it },
            label = { Text("Alert at Quantity") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                Text("Scan Barcode",
                    textAlign = TextAlign.Center)
            }

        }

        // Image capture and preview


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val newBox = box?.copy(
                    name = name.trim(),
                    description = description.trim(),
                    quantity = quantity.toLongOrNull() ?: 0,
                    alert = alertQuantity.toLongOrNull() ?: 0,
                    barcode = barcode.trim(),
                ) ?: Boxes( // or auto-generated if new
                    name = name.trim(),
                    orgId = orgId, // fill this from context or user prefs
                    barcode = barcode.trim(),
                    description = description.trim(),
                    boxId = null,
                    quantity = quantity.toLongOrNull() ?: 0,
                    alert = alertQuantity.toLongOrNull() ?: 0,
                    imageUrl = null
                )
                saveEnable = false
                onSave(newBox, (imageFile != null))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && quantity.isNotBlank() && saveEnable
        ) {
            Text(if (box == null) "Create Box" else "Save Changes")
        }
    }
}