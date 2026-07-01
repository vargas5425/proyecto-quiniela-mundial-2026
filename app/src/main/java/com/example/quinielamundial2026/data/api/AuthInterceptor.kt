package com.example.quinielamundial2026.data.api

import com.example.quinielamundial2026.QuinielaApplication
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        val token = QuinielaApplication.instance.preferencesManager.getToken()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}