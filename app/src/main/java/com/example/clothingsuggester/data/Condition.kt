package com.example.clothingsuggester.data

import com.google.gson.annotations.SerializedName

data class Condition(
    @SerializedName("text")val weatherStatus: String,
)