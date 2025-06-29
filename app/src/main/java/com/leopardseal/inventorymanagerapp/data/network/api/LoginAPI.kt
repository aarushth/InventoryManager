package com.leopardseal.inventorymanagerapp.data.network.api



import com.leopardseal.inventorymanagerapp.data.responses.dto.LoginResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {


    @POST("login")
    suspend fun login(
        @Body authToken: String
    ) : Response<LoginResponse>

    @POST("confirm_delete")
    suspend fun confirmDelete() : Response<Unit>

    @POST("cancel_delete")
    suspend fun cancelDelete() : Response<Unit>

}
