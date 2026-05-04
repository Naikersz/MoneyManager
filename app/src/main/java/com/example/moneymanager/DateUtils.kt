package com.example.moneymanager

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun isoToday(): String = isoDateFormatter().format(Date())
    
    fun isoDateFormatter(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun displayDateFormatter(): SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    fun parseIsoDate(iso: String?): Calendar? {
        if (iso.isNullOrBlank()) return null
        return try {
            val parts = iso.split("-")
            if (parts.size != 3) return null
            Calendar.getInstance().apply {
                set(Calendar.YEAR, parts[0].toIntOrNull() ?: return null)
                set(Calendar.MONTH, (parts[1].toIntOrNull() ?: return null) - 1)
                set(Calendar.DAY_OF_MONTH, parts[2].toIntOrNull() ?: return null)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun formatDisplayDate(iso: String?): String {
        if (iso.isNullOrBlank()) return "-"
        val calendar = parseIsoDate(iso) ?: return "-"
        return displayDateFormatter().format(calendar.time)
    }
    
    fun currentMonthLabel(): String {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return format.format(Date()).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}
