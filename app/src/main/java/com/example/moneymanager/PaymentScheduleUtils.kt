package com.example.moneymanager

internal fun getReminderDaysOptions(): List<Int> = listOf(0, 1, 3, 7)

internal fun sanitizeReminderDays(days: Int): Int {
    return getReminderDaysOptions().firstOrNull { it == days } ?: 1
}

internal fun isPaymentSkipped(skippedDates: List<String>?, dateIso: String?): Boolean {
    return !dateIso.isNullOrBlank() && skippedDates.orEmpty().contains(dateIso)
}

internal fun isPaymentPaid(paidDates: List<String>?, dateIso: String?): Boolean {
    return !dateIso.isNullOrBlank() && paidDates.orEmpty().contains(dateIso)
}
