package com.example.clothingsuggester

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingsuggester.data.Location
import com.example.clothingsuggester.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //val api_key = BuildConfig
    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeRequest()


    }

    private fun makeRequest() {
        Log.i(TAG, "Make")

        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(BASE_URL)
            .addQueryParameter(WEATHER_API_KEY, "123")
            .addQueryParameter(WEATHER_Q, "egypt")
            .build()

        val request = Request.Builder()
            .url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i(TAG, "${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val result = Gson().fromJson(it, Location::class.java)
                    runOnUiThread {
                        binding.textDate.text = result.country
                    }
                }
                Log.i(TAG, response.body?.string().toString())
            }

        })
    }

    companion object {
        const val TAG = "Main"
        const val SCHEME = "https"
        const val BASE_URL = "api.weatherapi.com/v1/current.json"

        //  const val WEATHER_API_KEY = BuildConfig.
        const val WEATHER_API_KEY = "key"
        const val WEATHER_Q = "q"
    }
}