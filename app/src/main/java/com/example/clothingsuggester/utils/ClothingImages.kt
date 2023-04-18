package com.example.clothingsuggester.utils

import com.example.clothingsuggester.R

object ClothingImages {
    fun getSummerClothes(): List<Int> {
        return listOf(R.drawable.summer1, R.drawable.summer2, R.drawable.summer3, R.drawable.summer4)
    }

    fun getWinterClothes(): List<Int> {
        return listOf(R.drawable.winter1, R.drawable.winter2, R.drawable.winter3, R.drawable.winter4)
    }

    fun getOtherClothes(): List<Int> {
        return listOf(R.drawable.other1, R.drawable.other2, R.drawable.other3, R.drawable.other4)
    }
}
