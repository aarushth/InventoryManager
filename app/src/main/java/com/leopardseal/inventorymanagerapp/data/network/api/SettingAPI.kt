package com.leopardseal.inventorymanagerapp.data.network.api

import retrofit2.Response
import retrofit2.http.GET

interface SettingAPI{

    @GET("version")
    suspend fun getVersion() : Response<String>
}