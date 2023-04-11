package com.example.clothingsuggester.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeFormat {
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDateString(dateString: String?): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(dateString, formatter)

        // Format the LocalDateTime object to "Apr 11" and "21:17"
        val monthDayFormatter = DateTimeFormatter.ofPattern("MMM dd")
        val dateFormatter = dateTime.format(monthDayFormatter)
        val hourMinuteFormatter = DateTimeFormatter.ofPattern("HH:mm a")
        val timeFormatter = dateTime.format(hourMinuteFormatter)

        return Pair(dateFormatter, timeFormatter)
    }
}