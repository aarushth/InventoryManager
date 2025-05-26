package com.leopardseal.inventorymanagerapp.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import com.leopardseal.inventorymanagerapp.data.network.api.ImageAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageApi : ImageAPI,
    @ApplicationContext private val context: Context,
): BaseRepository() {

    private val imageLoader = ImageLoader(context)


    @OptIn(ExperimentalCoilApi::class)
    suspend fun uploadImage(url : String, imageFile: File) = safeApiCall {
        val byteArray = imageFile.readBytes()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())

        val response = imageApi.uploadImageToBlob(
            sasUrl = url,
            image = requestBody
        )
        if(response.isSuccessful){
            Log.d("Upload", "success")
//            val destinationUrl = url.substringBefore("?")
            imageLoader.diskCache?.clear()
            imageLoader.memoryCache?.clear()
            imageFile.delete()
        }
        response
    }

}