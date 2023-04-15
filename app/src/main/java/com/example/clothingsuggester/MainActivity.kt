package com.example.clothingsuggester

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.example.clothingsuggester.data.WeatherData
import com.example.clothingsuggester.databinding.ActivityMainBinding
import com.example.clothingsuggester.utils.Constant
import com.example.clothingsuggester.utils.DateTimeFormat
import com.example.clothingsuggester.utils.MyHttpClient
import com.example.clothingsuggester.utils.SharedUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apikey = BuildConfig.API_KEY
    private val myHttpClient = MyHttpClient()
    private val sharedUtil = SharedUtil(this)
    private var summerClothes =
        listOf(R.drawable.summer1, R.drawable.summer2, R.drawable.summer3, R.drawable.summer4)
    private var winterClothes =
        listOf(R.drawable.winter1, R.drawable.winter2, R.drawable.winter3, R.drawable.winter4)
    private var otherClothes =
        listOf(R.drawable.other1, R.drawable.other2, R.drawable.other3, R.drawable.other4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun buildHttpUrl(location: String): HttpUrl {
        return Constant.BASE_URL.toHttpUrlOrNull()?.newBuilder()?.apply {
            addQueryParameter(Constant.WEATHER_API_KEY, apikey)
            addQueryParameter(Constant.WEATHER_QUERY_PARAM, "Sweden")
        }?.build()!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUi(weatherData: WeatherData) {
        val (formattedDate, formattedTime) = DateTimeFormat.parseDateString(weatherData.current.last_updated)

        binding.textDate.text = formattedDate
        binding.textTime.text = formattedTime
        binding.textWeatherCountry.text = weatherData.location.countryName
        binding.textWeatherCity.text = weatherData.location.cityName
        binding.textWeatherStatus.text = weatherData.current.condition?.weatherStatus
        binding.textWeatherDegree.text =
            getString(R.string.weather_degree, weatherData.current.temp_c.toString())
        val imageResource = getRandomImageForWeatherStatus(weatherData.current.temp_c!!.toInt())
        binding.imageClothes.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext, imageResource
            )
        )
        sharedUtil.saveWornClothes(setOf(imageResource.toString()))

    }

    private fun parseResponse(responseBody: String?): WeatherData {
        return Gson().fromJson(responseBody, WeatherData::class.java)
    }

    private fun makeRequest(location: String) {
        Log.i(TAG, "Make")
        showProgress(true)
        val httpUrl = buildHttpUrl(location)

        val request = Request.Builder().url(httpUrl).build()

        myHttpClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i(TAG, "${e.message}")
                showProgress(false)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string().toString()
                val weatherData = parseResponse(responseBody)
                runOnUiThread {
                    updateUi(weatherData)
                    showProgress(false)
                }
                Log.i(TAG, "responseBody : $responseBody")
            }

        })
    }

    @Suppress("DEPRECATION")
    private fun getLocationName(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                return sb.toString()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error getting location name: ${e.message}")
        }
        return null
    }

    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val locationName = getLocationName(location.latitude, location.longitude)
                    makeRequest(locationName!!)
                }
            }

        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
        )
    }

    private fun getRandomImageForWeatherStatus(weatherStatus: Int): Int {
        return when {
            weatherStatus > 20 -> {
                binding.weatherImage.setAnimation(R.raw.sunny)
                binding.weatherImage.playAnimation()
                summerClothes.random()
            }

            weatherStatus < 10 -> {
                binding.weatherImage.setAnimation(R.raw.winter)
                binding.weatherImage.playAnimation()
                winterClothes.random()
            }
            else -> {
                binding.weatherImage.setAnimation(R.raw.other)
                binding.weatherImage.playAnimation()
                otherClothes.random()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}