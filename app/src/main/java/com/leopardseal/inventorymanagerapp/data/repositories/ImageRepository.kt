package com.leopardseal.inventorymanagerapp.data.repositories

import android.content.Context
import android.net.Uri
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.ImageAPI
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageApi : ImageAPI
): BaseRepository() {

    suspend fun uploadImage(url : String, image : Uri, context: Context) = safeApiCall {
        imageApi.uploadImageToBlob(
            sasUrl = url,
            image = uriToRequestBody(context, image)
        )
    }
    fun uriToRequestBody(context: Context, uri: Uri): RequestBody {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes() ?: byteArrayOf()
        context.contentResolver.delete(uri, null, null)
        return byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
    }

    fun fileToRequestBody(imageFile : File) : RequestBody{
        requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        imageFile.delete()
    }
}