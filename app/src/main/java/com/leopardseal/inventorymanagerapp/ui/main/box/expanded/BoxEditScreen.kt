import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import com.leopardseal.inventorymanagerapp.ui.barcodeIcon
import com.leopardseal.inventorymanagerapp.ui.cameraIcon
import com.leopardseal.inventorymanagerapp.ui.main.box.expanded.BoxExpandedViewModel
import java.io.File
import java.util.UUID


@Composable
fun BoxEditScreen(
    viewModel: BoxExpandedViewModel = hiltViewModel(),
    navController: NavController,
    orgId : Long,
    onComplete : (boxId : Long) -> Unit,
    onUnauthorized: () -> Unit
) {
    val box by viewModel.box.collectAsState()
    val updateResponse by viewModel.updateResponse.collectAsState()
    val uploadImgResponse by viewModel.uploadResult.collectAsState()

    val context = LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle

    var name by rememberSaveable { mutableStateOf("") }
    var size by rememberSaveable { mutableStateOf("Large") }
    var barcode by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var imageFile by rememberSaveable  { mutableStateOf<File?>(null) }

    val initialized = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit){
        if(!initialized.value){
            viewModel.getBox()
        }
    }
    LaunchedEffect(box) {
        if (box != null && !initialized.value) {
            name = box!!.name.orEmpty()
            size = box!!.size.orEmpty()
            barcode = box!!.barcode.orEmpty()
            initialized.value = true
        }
    }
    val isNameValid = name.isNotBlank()

    val isSaveEnabled = if (box == null) {
        isNameValid
    } else {
        name != box!!.name ||
                size != box!!.size ||
                barcode != box!!.barcode.orEmpty() ||
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
            Toast.makeText(context, "Box saved", Toast.LENGTH_LONG).show()
            if((updateResponse as Resource.Success).value.imageUrl == null){
                viewModel.resetUpdateResponse()
                onComplete(box!!.id!!)

            }else{
                viewModel.uploadImage(
                    (updateResponse as Resource.Success).value.imageUrl!!,
                    imageFile!!
                )
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
            viewModel.resetUploadFlag()
            onComplete(box!!.id!!)
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
                    AsyncImage(
                        model = box?.imageUrl,
                        contentDescription = box?.name,
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

                Button(onClick = { navController.navigate("barcode")  }) {
                    Icon(barcodeIcon, contentDescription = "Scan Barcode")
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
                    viewModel.saveOrUpdateBox(newBox, (imageFile != null))
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