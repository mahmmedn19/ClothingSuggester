package com.example.clothingsuggester.data

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("region") val cityName: String?,
    @SerializedName("country") val countryName: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
)