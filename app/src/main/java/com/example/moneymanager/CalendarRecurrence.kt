package com.example.moneymanager

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CalendarRecurrence {
    private const val PERIOD_YEARLY = "yearly"

    fun monthlyOccurrenceMatches(startIso: String?, targetIso: String): Boolean {
        val start = parseIsoDate(startIso) ?: return false
        val target = parseIsoDate(targetIso) ?: return false
        if (target.before(start)) return false

        val expectedDay = minOf(start.get(Calendar.DAY_OF_MONTH), target.getActualMaximum(Calendar.DAY_OF_MONTH))
        return target.get(Calendar.DAY_OF_MONTH) == expectedDay
    }

    fun recurringOccurrenceMatches(startIso: String?, period: String, targetIso: String): Boolean {
        val start = parseIsoDate(startIso) ?: return false
        val target = parseIsoDate(targetIso) ?: return false
        if (target.before(start)) return false

        val anchorDay = start.get(Calendar.DAY_OF_MONTH)
        return if (period == PERIOD_YEARLY) {
            target.get(Calendar.MONTH) == start.get(Calendar.MONTH) &&
                target.get(Calendar.DAY_OF_MONTH) == minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
            val monthsDiff = (target.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
                (target.get(Calendar.MONTH) - start.get(Calendar.MONTH))
            monthsDiff >= 0 &&
                target.get(Calendar.DAY_OF_MONTH) == minOf(anchorDay, target.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
    }

    fun countMonthlyOccurrencesUntil(startIso: String?, targetIso: String): Int {
        val start = parseIsoDate(startIso) ?: return 0
        val target = parseIsoDate(targetIso) ?: return 0
        val anchorDay = start.get(Calendar.DAY_OF_MONTH)
        var count = 0
        while (!start.after(target)) {
            count++
            start.add(Calendar.MONTH, 1)
            start.set(Calendar.DAY_OF_MONTH, minOf(anchorDay, start.getActualMaximum(Calendar.DAY_OF_MONTH)))
        }
        return count
    }

    fun countRecurringOccurrencesUntil(
        startIso: String?,
        period: String,
        targetIso: String,
        skippedDates: List<String> = emptyList()
    ): Int {
        val occurrenceDate = parseIsoDate(startIso) ?: return 0
        val target = parseIsoDate(targetIso) ?: return 0
        val anchorDay = occurrenceDate.get(Calendar.DAY_OF_MONTH)
        var count = 0
        while (!occurrenceDate.after(target)) {
            val occurrenceIso = formatIsoDate(occurrenceDate)
            if (!skippedDates.contains(occurrenceIso)) {
                count++
            }
            if (period == PERIOD_YEARLY) {
                occurrenceDate.add(Calendar.YEAR, 1)
            } else {
                occurrenceDate.add(Calendar.MONTH, 1)
            }
            occurrenceDate.set(
                Calendar.DAY_OF_MONTH,
                minOf(anchorDay, occurrenceDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            )
        }
        return count
    }

    private fun parseIsoDate(value: String?): Calendar? {
        if (value.isNullOrBlank()) return null
        val parsed = runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value)
        }.getOrNull() ?: return null
        return runCatching {
            Calendar.getInstance().apply {
                time = parsed
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }.getOrNull()
    }

    private fun formatIsoDate(calendar: Calendar): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
}
