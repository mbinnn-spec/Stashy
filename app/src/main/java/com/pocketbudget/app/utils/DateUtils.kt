package com.pocketbudget.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val indonesianLocale = Locale("id", "ID")

    private val headerDateFormat = SimpleDateFormat("MMMM d", Locale.ENGLISH)
    private val fullDateFormat = SimpleDateFormat("d MMMM yyyy", indonesianLocale)
    private val dayGroupFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.ROOT)

    fun formatHeaderDate(timestamp: Long): String {
        val targetCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val nowCalendar = Calendar.getInstance()

        return if (targetCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
            targetCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)
        ) {
            "Today"
        } else if (targetCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
            targetCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR) - 1
        ) {
            "Yesterday"
        } else {
            headerDateFormat.format(Date(timestamp))
        }
    }

    fun formatFullDate(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun getDayGroupKey(timestamp: Long): String {
        return dayGroupFormat.format(Date(timestamp))
    }

    fun getStartOfMonth(calendar: Calendar = Calendar.getInstance()): Long {
        return calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfMonth(calendar: Calendar = Calendar.getInstance()): Long {
        return calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}
