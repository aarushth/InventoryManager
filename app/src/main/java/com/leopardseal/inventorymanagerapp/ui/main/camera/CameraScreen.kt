package com.leopardseal.inventorymanagerapp.ui.main.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu.OnDismissListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File

@Composable
fun CameraScreen(
    navController: NavController
) {
    val context = LocalContext.current


    var shouldNavigateBack by remember { mutableStateOf(false) }
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var dismissed by remember { mutableStateOf(false) }

    if (shouldNavigateBack && capturedFile != null && capturedUri != null) {
        LaunchedEffect("navigate_back_after_capture") {
            navController.previousBackStackEntry?.savedStateHandle?.set("imageFile", capturedFile)
            navController.previousBackStackEntry?.savedStateHandle?.set("imageUri", capturedUri)
            navController.popBackStack()
        }
    }
    if (dismissed) {
        LaunchedEffect("navigate_back_after_dismiss") {
            navController.popBackStack()
        }
    }
    CameraCaptureHandler(
        context = context,
        onImageCaptured = { file, uri -> capturedFile = file
            capturedUri = uri
            shouldNavigateBack = true},
        onDismiss = {dismissed = true }
    )
}

@Composable
fun CameraCaptureHandler(
    context: Context = LocalContext.current,
    onImageCaptured: (imageFile: File, imageUri: Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val imageFile = remember { mutableStateOf<File?>(null) }

    // This will hold the URI passed to TakePicture()
    val imageUri = remember {
        val file = File.createTempFile("temp_image_", ".jpg", context.cacheDir)
        imageFile.value = file
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageFile.value != null) {
            onImageCaptured(imageFile.value!!, imageUri)
        } else {
            imageFile.value?.delete()
            onDismiss()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(imageUri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            launcher.launch(imageUri)
        }
    }
}
