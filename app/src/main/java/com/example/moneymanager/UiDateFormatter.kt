package com.example.moneymanager

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UiDateFormatter {

    fun isoDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    fun displayDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    }

    fun homeMonthFormatter(): SimpleDateFormat {
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    }

    fun parseIsoDate(value: String?): Calendar? {
        if (value.isNullOrBlank()) return null
        return runCatching {
            Calendar.getInstance().apply {
                time = isoDateFormatter().parse(value)
                    ?: displayDateFormatter().parse(value)
                    ?: Date()
            }
        }.getOrNull()
    }

    fun formatDisplayDate(value: String?): String {
        val parsed = parseIsoDate(value)?.time ?: return "-"
        return displayDateFormatter().format(parsed)
    }

    fun isoDateWithOffset(days: Int): String {
        return isoDateFormatter().format(
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, days)
            }.time
        )
    }

    fun normalizedCalendar(value: Calendar): Calendar {
        return (value.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun todayCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun isSameMonth(value: Calendar?, month: Calendar): Boolean {
        return value != null &&
            value.get(Calendar.YEAR) == month.get(Calendar.YEAR) &&
            value.get(Calendar.MONTH) == month.get(Calendar.MONTH)
    }
}
