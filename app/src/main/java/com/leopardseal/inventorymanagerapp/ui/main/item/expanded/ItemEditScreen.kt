
import android.net.Uri
import android.widget.Toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import com.leopardseal.inventorymanagerapp.data.responses.Item
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse

import java.io.File
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.leopardseal.inventorymanagerapp.ui.barcodeIcon
import com.leopardseal.inventorymanagerapp.ui.cameraIcon
import com.leopardseal.inventorymanagerapp.ui.main.item.expanded.ItemExpandedViewModel


@Composable
fun ItemEditScreen(
    viewModel : ItemExpandedViewModel = hiltViewModel(),
    innerPaddingValues: PaddingValues,
    navController: NavController,
    orgId : Long,
    onComplete : (itemId : Long) -> Unit,
    onUnauthorized: () -> Unit
) {
    val item by viewModel.item.collectAsState()
    val updateResponse by viewModel.updateResponse.collectAsState()
    val uploadImgResponse by viewModel.uploadResult.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current

    val savedStateHandle : SavedStateHandle? = currentBackStackEntry?.savedStateHandle

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("0") }
    var alertQuantity by rememberSaveable {mutableStateOf("0") }
    var barcode by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var imageFile by rememberSaveable  { mutableStateOf<File?>(null) }

    val initialized = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit){
        if(!initialized.value){
            viewModel.getItem()
        }
    }

    LaunchedEffect(item) {
        if (item!= null && !initialized.value) {
            name = item!!.name.orEmpty()
            description = item!!.description.orEmpty()
            barcode = item!!.barcode.orEmpty()
            quantity = item!!.quantity.toString()
            alertQuantity = item!!.alert.toString()
            initialized.value = true
        }
    }

    val isQuantityValid = quantity.isNotBlank() && quantity.all { it.isDigit() }
    val isNameValid = name.isNotBlank()

    val isSaveEnabled = if (item == null) {
        isNameValid && isQuantityValid
    } else {
        name != item!!.name ||
                description != item!!.description ||
                quantity != item!!.quantity.toString() ||
                alertQuantity != item!!.alert.toString() ||
                barcode != item!!.barcode.orEmpty() ||
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
            Toast.makeText(context, "Item saved", Toast.LENGTH_LONG).show()
            item!!.id?.let {
                if((updateResponse as Resource.Success).value.imageUrl == null){
                    viewModel.resetUpdateResponse()
                    onComplete(item!!.id!!)
                }else{
                    viewModel.uploadImage((updateResponse as Resource.Success).value.imageUrl!!,imageFile!!)
                }
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
            viewModel.resetUploadFlag()
            onComplete(item!!.id!!)

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
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    AsyncImage(
                        model = item?.imageUrl,
                        contentDescription = item?.name,
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
                label = { Text("Item Name") },
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
            val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
            Button(
                onClick = {
                    val newItem = item?.copy(
                        name = name.trim(),
                        description = description.trim(),
                        quantity = quantity.toLongOrNull() ?: 0,
                        alert = alertQuantity.toLongOrNull() ?: 0,
                        barcode = barcode.trim(),
                    ) ?: Item( // or auto-generated if new
                        name = name.trim(),
                        orgId = orgId, // fill this from context or user prefs
                        barcode = barcode.trim(),
                        description = description.trim(),
                        boxId = null,
                        quantity = quantity.toLongOrNull() ?: 0,
                        alert = alertQuantity.toLongOrNull() ?: 0,
                        imageUrl = null
                    )
                    viewModel.saveOrUpdateItem(newItem, (imageFile != null))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .consumeWindowInsets(innerPaddingValues)
                    .imePadding()
                    .fillMaxWidth(),
            ) {
                Text(if (item == null) "Create Item" else "Save Changes")
            }
        }
    }
}