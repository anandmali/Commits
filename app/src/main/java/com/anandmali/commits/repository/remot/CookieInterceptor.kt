package com.anandmali.commits.repository.remot

import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val url = request.url.toString()

        if (request.header("Add-Cookie-Authentication") != null) {
            val response = chain.proceed(request)
        }

        return chain.proceed(request)
    }
}