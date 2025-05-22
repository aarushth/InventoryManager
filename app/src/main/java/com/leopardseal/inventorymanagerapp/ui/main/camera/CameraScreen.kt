package com.leopardseal.inventorymanagerapp.ui.main.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun CameraScreen(
    onImageCaptured: (imageFile: File, imageUri: Uri) -> Unit
) {
    val context = LocalContext.current

    CameraCaptureHandler(
        context = context,
        onImageCaptured = { file, uri -> onImageCaptured(file, uri)

        }
    )
}

@Composable
fun CameraCaptureHandler(
    context: Context = LocalContext.current,
    onImageCaptured: (imageFile: File, imageUri: Uri) -> Unit
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
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(imageUri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            launcher.launch(imageUri)
        }
    }
}
