package com.example.clothingsuggester.utils

import com.example.clothingsuggester.BuildConfig

object Constant {
    private const val SCHEME = "https"
    private const val HOST = "api.weatherapi.com"
    const val BASE_URL = "$SCHEME://$HOST/v1/current.json"
    const val WEATHER_API_KEY = "key"
    const val WEATHER_QUERY_PARAM = "q"
    const val API_KEY = BuildConfig.API_KEY
}