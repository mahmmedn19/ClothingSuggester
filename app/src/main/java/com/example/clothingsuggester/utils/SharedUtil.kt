package com.example.clothingsuggester.utils

import android.content.Context

class SharedUtil(private val context: Context) {

    private val PREFS_NAME = "MyPrefs"
    private val WORN_CLOTHES_KEY = "wornClothes"

    fun saveWornClothes(clothes: Set<String>) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putStringSet(WORN_CLOTHES_KEY, clothes).apply()
    }

    fun getWornClothes(): Set<String> {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getStringSet(WORN_CLOTHES_KEY, emptySet()) ?: emptySet()
    }
}
