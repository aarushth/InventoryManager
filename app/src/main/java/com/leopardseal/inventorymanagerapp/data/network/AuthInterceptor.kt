package com.leopardseal.inventorymanagerapp.data.network

import okhttp3.Interceptor

import okhttp3.Response

class AuthInterceptor(private val authToken: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!authToken.isNullOrEmpty()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(request)
    }

}