package com.example.clothingsuggester.utils

object Constant {
    private const val SCHEME = "https"
    private const val HOST = "api.weatherapi.com"
    const val BASE_URL = "$SCHEME://$HOST/v1/current.json"
    const val WEATHER_API_KEY = "key"
    const val WEATHER_QUERY_PARAM = "q"
}