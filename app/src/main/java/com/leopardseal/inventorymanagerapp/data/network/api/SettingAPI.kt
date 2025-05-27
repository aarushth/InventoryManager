package com.leopardseal.inventorymanagerapp.data.network.api

interface SettingAPI{

    @GET("version")
    suspend fun getVersion() : Response<String>
}