package com.example.clothingsuggester

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingsuggester.data.WeatherData
import com.example.clothingsuggester.databinding.ActivityMainBinding
import com.example.clothingsuggester.utils.Constant
import com.example.clothingsuggester.utils.DateTimeFormat
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val apikey = BuildConfig.API_KEY
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeRequest()


    }

    private fun makeRequest() {
        Log.i(TAG, "Make")

        val httpUrl = Constant.BASE_URL.toHttpUrlOrNull()?.newBuilder()?.apply {
            addQueryParameter(Constant.WEATHER_API_KEY, apikey)
            addQueryParameter(Constant.WEATHER_QUERY_PARAM, "egypt")
        }?.build()

        val request = Request.Builder()
            .url(httpUrl!!).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i(TAG, "${e.message}")
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string().toString()
                val weatherData = parseResponse(responseBody)
                runOnUiThread {
                    val (formattedDate, formattedTime) = DateTimeFormat.parseDateString(weatherData.location.localDateTime)
                    binding.textDate.text = formattedDate
                    binding.textTime.text = formattedTime


                }
                Log.i(TAG, "responseBody : $responseBody")

            }

        })
    }

    private fun parseResponse(responseBody: String?): WeatherData {
        return Gson().fromJson(responseBody, WeatherData::class.java)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}