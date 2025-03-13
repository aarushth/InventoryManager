package com.leopardseal.inventorymanagerapp.network

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.markedForInjection()) {
            val token = tokenProvider.invoke()
            if (!token.isNullOrEmpty()) {
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return chain.proceed(request)
    }

    
    private fun Request.markedForInjection(): Boolean = tag<Invocation>()?.method()
        ?.annotations?.toSet()?.find { it is InjectAuth } != null
}