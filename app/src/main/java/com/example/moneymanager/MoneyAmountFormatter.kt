package com.example.moneymanager

import java.util.Locale
import kotlin.math.abs

class MoneyAmountFormatter(private val currencySymbolProvider: () -> String) {

    fun formatAmount(value: Double): String {
        return "%.2f %s".format(value, currencySymbolProvider())
    }

    fun formatSignedAmount(value: Double): String {
        if (abs(value) < 0.005) {
            return formatAmount(0.0)
        }
        return if (value > 0) {
            "+${formatAmount(value)}"
        } else {
            "-${formatAmount(abs(value))}"
        }
    }

    fun formatCompactCalendarAmount(value: Double): String {
        return formatCompactSignedAmount(value).ifBlank { "0${currencySymbolProvider()}" }
    }

    fun formatCompactAmount(value: Double): String {
        return formatCompactSignedAmount(value)
            .removePrefix("+")
            .removePrefix("-")
            .ifBlank { "0${currencySymbolProvider()}" }
    }

    fun formatCompactSignedAmount(value: Double): String {
        if (abs(value) < 0.005) return ""
        val sign = if (value > 0) "+" else "-"
        val amount = abs(value)
        val amountText = if (amount >= 1000.0) {
            String.format(Locale.US, "%.1fk", amount / 1000.0).replace(".0k", "k")
        } else {
            String.format(Locale.US, "%.0f", amount)
        }
        return "$sign$amountText${currencySymbolProvider()}"
    }

    fun formatCompactSignedTotal(value: Double): String {
        return formatCompactSignedAmount(value).ifBlank { "0${currencySymbolProvider()}" }
    }
}
