package com.example.clothingsuggester.data

import com.example.clothingsuggester.data.Condition
import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("condition")val condition: Condition?,
    @SerializedName("temp_c")val tempCelsius: Double?,
)