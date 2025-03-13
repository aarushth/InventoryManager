package com.leopardseal.inventorymanagerapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerComms {
    companion object{
        private const val BASE_URL = "192.168.68.54:8080"
    }
    lateinit val token : String
    val tokenProvider: () -> String? = { 
        return token
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
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}