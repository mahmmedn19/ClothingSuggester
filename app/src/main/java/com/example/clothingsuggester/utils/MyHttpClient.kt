package com.example.clothingsuggester.utils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.internal.http2.Http2Reader
import okhttp3.logging.HttpLoggingInterceptor

class MyHttpClient {
    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            HttpLoggingInterceptor.Logger { message ->
                Log.d("OkHttp", message)
            }
        })
        .build()
}
