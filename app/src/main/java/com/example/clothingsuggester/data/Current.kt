package com.example.clothingsuggester.data

import com.example.clothingsuggester.data.Condition

data class Current(
    val condition: Condition?,
    val temp_c: Double?,
    val temp_f: Double?
)