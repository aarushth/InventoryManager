package com.leopardseal.inventorymanagerapp.network

import com.developer.gbuttons.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerComms {
    companion object{
        private const val BASE_URL = "http://192.168.68.56:8080"
    }
    private lateinit var token : String
    val tokenProvider: () -> String? = { 
        token
    }
    fun setToken(tok: String){
        token = tok
    } 

    fun <Api> buildApi(
        api:Class<Api>
    ):Api {

        val okHttpClient = OkHttpClient.Builder()
            .also { client -> 
                if(BuildConfig.DEBUG){
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}