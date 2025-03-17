package com.leopardseal.inventorymanagerapp.data.network


import com.leopardseal.inventorymanagerapp.data.responses.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginAPI {

    @GET("login")
    suspend fun login(
         @Header("authToken") authToken: String
    ) :Any
}