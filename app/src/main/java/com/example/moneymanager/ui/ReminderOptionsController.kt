package com.example.moneymanager.ui

import android.content.Context
import android.widget.Spinner
import com.example.moneymanager.R
import com.example.moneymanager.getReminderDaysOptions

internal class ReminderOptionsController(
    private val context: Context
) {
    fun getReminderDaysLabels(): List<String> {
        return getReminderDaysOptions().map { days ->
            when (days) {
                0 -> context.getString(R.string.payment_reminder_same_day)
                1 -> context.getString(R.string.payment_reminder_one_day)
                else -> context.getString(R.string.payment_reminder_days_before, days)
            }
        }
    }

    fun setReminderDaysSelection(spinner: Spinner, daysBefore: Int) {
        val index = getReminderDaysOptions().indexOf(daysBefore).takeIf { it >= 0 } ?: 1
        spinner.setSelection(index)
    }

    fun getSelectedReminderDays(spinner: Spinner): Int {
        return getReminderDaysOptions().getOrElse(spinner.selectedItemPosition) { 1 }
    }

    fun appendPaymentStatus(detail: String, isPaid: Boolean, isSkipped: Boolean): String {
        val status = when {
            isSkipped -> context.getString(R.string.payment_status_skipped)
            isPaid -> context.getString(R.string.payment_status_paid)
            else -> return detail
        }
        return "$detail \u00B7 $status"
    }
}
