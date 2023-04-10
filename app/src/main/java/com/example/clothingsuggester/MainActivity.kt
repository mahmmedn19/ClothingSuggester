package com.example.clothingsuggester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewbinding.BuildConfig
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //val api_key = BuildConfig
    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeRequest()


    }

    private fun makeRequest() {
        Log.i("Main","Make")
        val request = Request.Builder().url("https://api.weatherapi.com/v1/current.json?key=8454ad4865ef4dc28a9195902230904&q=Tanta")
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.i("Main","${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Main",response.body?.string().toString())
            }

        })
    }
}