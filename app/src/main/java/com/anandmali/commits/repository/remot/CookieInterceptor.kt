package com.anandmali.commits.repository.remot

import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        TODO("Pagination can be implemented here, to fetch next page url from response header")
    }
}