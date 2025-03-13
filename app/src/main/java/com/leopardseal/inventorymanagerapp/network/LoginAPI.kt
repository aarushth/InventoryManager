package com.leopardseal.inventorymanagerapp.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPI {
    @InjectAuth
    @FormUrlEncoded
    @POST("signIn")
    fun login(
        // @Header("Authorization") authToken: String
        // @Field("authToken") authToken: String
    ) :Any
}