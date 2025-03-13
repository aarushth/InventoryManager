package com.leopardseal.inventorymanagerapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerComms {
    companion object{
        private const val BASE_URL = "192.168.68.54:8080"
    }

    fun <Api> buildApi(
        api:Class<Api>
    ):Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}