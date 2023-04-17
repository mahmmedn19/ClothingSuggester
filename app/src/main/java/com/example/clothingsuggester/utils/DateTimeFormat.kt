package com.example.clothingsuggester.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeFormat {
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDateString(dateString: String?): Pair<String, String> {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        val monthDayFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        val dateFormatter = monthDayFormatter.format(date)

        val hourMinuteFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeFormatter = hourMinuteFormatter.format(date)

        return Pair(dateFormatter, timeFormatter)
    }
}