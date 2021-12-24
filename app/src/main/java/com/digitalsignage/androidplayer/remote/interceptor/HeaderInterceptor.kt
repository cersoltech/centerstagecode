package com.digitalsignage.androidplayer.remote.interceptor

import com.digitalsignage.androidplayer.utils.AppLog
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.Exception

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        return try {
            request = request.newBuilder().apply {
                addHeader("AuthorizeToken","")
            }.build()
            chain.proceed(request)
        }catch (e: Exception)
        {
            AppLog.e("TokenInterceptor: Error",e.message.toString())
            chain.proceed(request)
        }
    }
}