package com.leopardseal.inventorymanagerapp.data.network


import com.google.gson.GsonBuilder
import com.leopardseal.inventorymanagerapp.data.UserPreferences

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ServerComms @Inject constructor(){
    companion object{
        //use this when using production server
//        private const val BASE_URL = "https://inventory-manager-backend-a2or.onrender.com"

//        use this when testing on local server, change to pc ip
        private const val BASE_URL = "http://192.168.68.68:8080"
    }


    fun <Api> buildApi(
        api:Class<Api>,
        userPreferences: UserPreferences
    ):Api {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(AuthInterceptor(userPreferences))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(api)
    }
    fun <Api> buildImageApi(
        api:Class<Api>
    ):Api {

        val azureOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()) // optional for debugging
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://dummy.base.url/") // Required, but overridden by @Url
            .client(azureOkHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(api)
    }

}