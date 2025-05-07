package com.leopardseal.inventorymanagerapp.data.network.API


import com.leopardseal.inventorymanagerapp.data.responses.MyUsers
import retrofit2.http.GET
import retrofit2.http.Header

interface LoginAPI {

    @GET("login")
    suspend fun login(
         @Header("Authorization") authToken: String
    ) : LoginResponse
}
