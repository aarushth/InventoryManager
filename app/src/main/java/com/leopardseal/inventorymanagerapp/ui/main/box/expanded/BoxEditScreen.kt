import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import java.io.File
import java.util.*
import kotlinx.coroutines.flow.collect



@Composable
fun BoxEditScreen(
    box: Boxes?, // null = new box
    updateResponse: Resource<SaveResponse>,
    uploadImgResponse: Resource<Unit>,
    currentBackStackEntry: NavBackStackEntry?,
    orgId : Long,
    onImageCapture: () -> Unit,
    onSave: (Boxes, Boolean) -> Unit,
    onSaveComplete: (imageFile : File?) -> Unit,
    onUnauthorized: () -> Unit,
    onImageSaved: () -> Unit,
    onScanBarcodeClick: () -> Unit
) {
    val context = LocalContext.current
    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle

    var name by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("name") ?: box?.name.orEmpty()) }
    var size by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("size") ?: box?.size?:"Large") }
    var barcode by rememberSaveable { mutableStateOf(savedStateHandle?.get<String>("barcode") ?: box?.barcode.orEmpty()) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(savedStateHandle?.get<Uri>("imageUri")) }
    var imageFile by rememberSaveable  { mutableStateOf<File?>(savedStateHandle?.get<File>("imageFile")) }

    val isNameValid = name.isNotBlank()

    val isSaveEnabled = if (box == null) {
        isNameValid
    } else {
        name != box.name ||
                size != box.size ||
                barcode != box.barcode.orEmpty() ||
                imageFile != null
    }
    LaunchedEffect(name) { savedStateHandle?.set("name", name) }
    LaunchedEffect(size) { savedStateHandle?.set("description", size) }
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
            Toast.makeText(context, "Box saved", Toast.LENGTH_LONG).show()
            box!!.id?.let { onSaveComplete(imageFile) }
        }
        is Resource.Failure -> {
            if (updateResponse.isNetworkError) {
                Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
            } else if (updateResponse.errorCode == HttpStatus.SC_UNAUTHORIZED) {
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
    Box(modifier = Modifier.fillMaxSize()) {
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
                            data = if (box != null) {
                                box!!.imageUrl + "?t=${System.currentTimeMillis()}"
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
                label = { Text("Box Name") },
                modifier = Modifier.fillMaxWidth()
            )

            SizeDropdownMenu(
                selectedSize = size,
                onSizeSelected = { size = it }
            )

            Text("You selected: $size")

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
        if (isSaveEnabled) {
            Button(
                onClick = {
                    val newBox = box?.copy(
                        name = name.trim(),
                        barcode = barcode.trim(),
                        size = size
                    ) ?: Boxes( // or auto-generated if new
                        name = name.trim(),
                        orgId = orgId, // fill this from context or user prefs
                        barcode = barcode.trim(),
                        imageUrl = null,
                        id = null,
                        locationId = null,
                        size = size
                    )
                    onSave(newBox, (imageFile != null))
                },
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            ) {
                Text(if (box == null) "Create Box" else "Save Changes")
            }
        }
    }
}

    @Composable
    fun SizeDropdownMenu(
        selectedSize: String,
        onSizeSelected: (String) -> Unit,
    ) {
        val sizeOptions = listOf("Large", "Medium", "Small")
        var expanded by remember { mutableStateOf(false) }
        val textFieldSize = remember { mutableStateOf(IntSize.Zero) }

        val interactionSource = remember { MutableInteractionSource() }
        val indication = LocalIndication.current
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedSize,
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    expanded = true
                                }
                            }
                        }
                    },
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Size") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize.value = coordinates.size
                    },
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.value.width.toDp() }) // match width
            ) {
                sizeOptions.forEach { sizeOpt ->
                    DropdownMenuItem(
                        text = { Text(sizeOpt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .indication(interactionSource, indication)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {}
                            ),
                        onClick = {
                            onSizeSelected(sizeOpt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }