package com.mbj.doeat.util

import android.os.Build
import com.mbj.doeat.util.Constants.CURRENT_DATE_PATTERN
import com.mbj.doeat.util.Constants.DATE_OUTPUT_PATTERN
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun getCurrentTime(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentLocalDateTime: LocalDateTime = LocalDateTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern(CURRENT_DATE_PATTERN)
            val formattedTime = currentLocalDateTime.format(timeFormatter)
            formattedTime
        } else {
            val currentTimeMillis = System.currentTimeMillis()
            val currentTime = Date(currentTimeMillis)
            val timeFormat = SimpleDateFormat(CURRENT_DATE_PATTERN, Locale.getDefault())
            val formattedTime = timeFormat.format(currentTime)
            formattedTime
        }
    }

    fun getFormattedElapsedTime(createdDate: String): String {
        val dateFormat = SimpleDateFormat(CURRENT_DATE_PATTERN, Locale.getDefault())

        val currentDate = dateFormat.parse(getCurrentTime())
        val startDate = dateFormat.parse(createdDate)

        val diffInMillis = currentDate.time - startDate.time

        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24
        val diffInMonths = diffInDays / 30
        val diffInYears = diffInDays / 365

        val result = when {
            diffInYears > 0 -> "${diffInYears}년 전"
            diffInMonths > 0 -> "${diffInMonths}달 전"
            diffInDays > 0 -> "${diffInDays}일 전"
            diffInHours > 0 -> "${diffInHours}시간 전"
            diffInMinutes > 0 -> "${diffInMinutes}분 전"
            else -> "방금 전"
        }

        return result
    }

    fun formatCustomDate(createdDate: String, chatRoomDetailType: Boolean? = true): String {
        val currentDate = Calendar.getInstance()
        val parsedDate = SimpleDateFormat(CURRENT_DATE_PATTERN, Locale.getDefault()).parse(createdDate)

        val calendar = Calendar.getInstance()
        calendar.time = parsedDate

        val outputFormat = SimpleDateFormat(DATE_OUTPUT_PATTERN, Locale.getDefault())

        if (currentDate[Calendar.DATE] == calendar[Calendar.DATE]) {
            // 오늘
            return outputFormat.format(parsedDate)
        }

        currentDate.add(Calendar.DATE, -1)
        if (currentDate[Calendar.DATE] == calendar[Calendar.DATE]) {
            // 어제
            return if(chatRoomDetailType == true) {
                "어제 ${outputFormat.format(parsedDate)}"
            } else {
                "어제"
            }
        }

        if (currentDate[Calendar.YEAR] == calendar[Calendar.YEAR]) {
            // 같은 년도
            val month = SimpleDateFormat("M", Locale.getDefault()).format(parsedDate)
            val day = SimpleDateFormat("d", Locale.getDefault()).format(parsedDate)
            return "${month}월 ${day}일"
        }

        // 다른 년도
        return SimpleDateFormat("yyyy.M.d.", Locale.getDefault()).format(parsedDate)
    }
}
