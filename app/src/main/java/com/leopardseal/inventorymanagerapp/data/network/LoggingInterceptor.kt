package com.leopardseal.inventorymanagerapp.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val tag = "API_LOG"

        // Log request details
        Log.d(tag, "➡️ Request: ${request.method} ${request.url}")
        Log.d(tag, "Headers: ${request.headers}")

        if (request.body != null) {
            val buffer = okio.Buffer()
            request.body!!.writeTo(buffer)
            Log.d(tag, "Body: ${buffer.readUtf8()}")
        }

        val response = chain.proceed(request)

        // Log response details
        Log.d(tag, "⬅️ Response: ${response.code} ${response.message} for ${response.request.url}")

        return response
    }
}