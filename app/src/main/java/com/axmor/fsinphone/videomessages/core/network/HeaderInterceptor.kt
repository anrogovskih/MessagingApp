package com.axmor.fsinphone.videomessages.core.network

import com.axmor.fsinphone.videomessages.core.preferences.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val prefs: AppPreferences): Interceptor {

    companion object {
        private const val API_HEADER = "X-API-TOKEN"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val token = prefs.accessToken
        if (token.isNotEmpty())
            builder.addHeader(API_HEADER, token)

        return chain.proceed(builder.build())
    }
}