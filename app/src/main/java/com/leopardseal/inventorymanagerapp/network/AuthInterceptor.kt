package com.leopardseal.inventorymanagerapp.network

import okhttp3.Interceptor

import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = tokenProvider.invoke()
        if (!token.isNullOrEmpty()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }

}