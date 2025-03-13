package com.leopardseal.inventorymanagerapp.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPI {

    @FormUrlEncoded
    @POST("signIn")
    fun login(
        @Field("authToken") authToken: String
    ) :Any
}