package com.leopardseal.inventorymanagerapp.network


import com.leopardseal.inventorymanagerapp.responses.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginAPI {

    @GET("login")
    suspend fun login(
        // @Field("authToken") authToken: String
    ) :Any
}