package com.example.clothingsuggester

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
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
import com.example.clothingsuggester.utils.ClothingImages
import com.example.clothingsuggester.utils.Constant
import com.example.clothingsuggester.utils.DateTimeFormat
import com.example.clothingsuggester.utils.SharedUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val logInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    private val myHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(logInterceptor)
    }.build()

    private val sharedUtil = SharedUtil(this)
    private var summerClothes = ClothingImages.getSummerClothes()
    private var winterClothes = ClothingImages.getWinterClothes()
    private var otherClothes = ClothingImages.getOtherClothes()


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
            addQueryParameter(Constant.WEATHER_API_KEY, Constant.API_KEY)
            addQueryParameter(Constant.WEATHER_QUERY_PARAM, location)
        }?.build()!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUi(weatherData: WeatherData) {
        val (formattedDate, formattedTime) = DateTimeFormat.parseDateString(weatherData.location.localTimeAndDate)

        binding.textDate.text = formattedDate
        binding.textTime.text = formattedTime
        binding.textWeatherCountry.text = weatherData.location.countryName
        binding.textWeatherCity.text = weatherData.location.cityName
        binding.textWeatherStatus.text = weatherData.current.condition?.weatherStatus
        binding.textWeatherDegree.text =
            getString(R.string.weather_degree, weatherData.current.tempCelsius.toString())
        getUpdatedWornClothesImage(
            weatherData.current.tempCelsius!!.toInt(),
            weatherData.current.condition!!.weatherStatus
        )
    }

    private fun parseResponse(responseBody: String?): WeatherData {
        return Gson().fromJson(responseBody, WeatherData::class.java)
    }

    private fun makeRequest(lat: Double, long: Double) {
        Log.i(TAG, "Make")
        showProgress(true)
        val httpUrl = buildHttpUrl("${lat},${long}")
        val request = Request.Builder().apply { }.url(httpUrl).build()
        myHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.i(TAG, "${e.message}")
                showProgress(false)
                runOnUiThread {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Failed to make network request: ${e.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
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


    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    //val locationName = getLocationName(location.latitude, location.longitude)
                    makeRequest(location.latitude, location.longitude)
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

    private fun getUpdatedWornClothesImage(weatherStatus: Int, weatherText: String) {
        val wornClothesSet = sharedUtil.getWornClothes()
        val newWornClothesSet = mutableSetOf<String>()
        val imageResource = getRandomImageForWeatherStatus(weatherStatus, weatherText)

        if (wornClothesSet.contains(imageResource.toString())) {
            val filteredWornClothesSet = wornClothesSet.filter { it != imageResource.toString() }
            val newImageResource = if (filteredWornClothesSet.isEmpty()) {
                getRandomImageForWeatherStatus(weatherStatus, weatherText)
            } else {
                filteredWornClothesSet.random().toInt()
            }
            binding.imageClothes.setImageDrawable(
                AppCompatResources.getDrawable(
                    applicationContext, newImageResource
                )
            )
            newWornClothesSet.addAll(filteredWornClothesSet)
            newWornClothesSet.add(newImageResource.toString())
        } else {
            binding.imageClothes.setImageDrawable(
                AppCompatResources.getDrawable(
                    applicationContext, imageResource
                )
            )
            newWornClothesSet.addAll(wornClothesSet)
            newWornClothesSet.add(imageResource.toString())
        }
        sharedUtil.saveWornClothes(newWornClothesSet)
    }

    private fun getRandomImageForWeatherStatus(weatherStatus: Int, weatherText: String): Int {
        val clothes = when {
            weatherStatus > 20 -> summerClothes
            weatherStatus < 10 -> winterClothes
            else -> otherClothes
        }

        val animation = when (clothes) {
            summerClothes -> R.raw.sunny
            winterClothes -> R.raw.winter
            else -> R.raw.other
        }

        val weatherSky = when (weatherText) {
            "Sunny" -> R.drawable.sunny_background
            "Party Cloud" -> R.drawable.party_cloud_background
            else -> R.drawable.sunny_background
        }

        binding.weatherImage.setAnimation(animation)
        binding.weatherImage.playAnimation()

        binding.viewBackground.setBackgroundResource(weatherSky)

        return clothes.random()
    }

    companion object {
        const val TAG = "MainActivity"
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}