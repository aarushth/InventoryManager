package com.leopardseal.inventorymanagerapp.data.network.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

interface ImageAPI {
        @PUT
        suspend fun uploadImageToBlob(
            @Url sasUrl: String,
            @Header("x-ms-blob-type") blobType: String = "BlockBlob",
            @Body image: RequestBody
        )
        : Response<Unit>
}