package com.leopardseal.inventorymanagerapp.data.network

import com.developer.gbuttons.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerComms {
    companion object{
        //use this when using production server
        private const val BASE_URL = "https://inventory-manager-backend-a2or.onrender.com"

//        use this when testing on local server, change to pc ip
//        private const val BASE_URL = "http://172.24.160.1:8001"
    }


    fun <Api> buildApi(
        api:Class<Api>,
        authToken: String? = null
    ):Api {
        val okHttpClient = OkHttpClient.Builder()
//            .also { client ->
//                if(BuildConfig.DEBUG){
//                    val logging = HttpLoggingInterceptor()
//                    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
//                    client.addInterceptor(logging)
//                }
//            }
            .addInterceptor(AuthInterceptor(authToken))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}