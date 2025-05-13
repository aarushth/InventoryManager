package com.leopardseal.inventorymanagerapp.data.network

import com.leopardseal.inventorymanagerapp.data.UserPreferences

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor

import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = getTokenFromPreferences()
//        val token = ""
        if (!token.isNullOrEmpty()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(request)
    }

    private fun getTokenFromPreferences(): String? {
        // Retrieve the token asynchronously from DataStore (using a suspend function)
        var token: String? = null
        runBlocking {
            token = userPreferences.authToken.firstOrNull()
        }
        return token
    }
}