package com.example.moneymanager.ui

import android.content.Context
import com.example.moneymanager.R
import com.example.moneymanager.THEME_HELL

internal class SettingsDisplayController(
    private val context: Context
) {
    fun getLanguageLabel(languageCode: String): String {
        return when (languageCode) {
            "ru" -> context.getString(R.string.russian)
            "de" -> context.getString(R.string.german)
            else -> context.getString(R.string.english)
        }
    }

    fun getThemeLabel(theme: String): String {
        return if (theme == THEME_HELL) {
            context.getString(R.string.theme_dark)
        } else {
            context.getString(R.string.theme_light)
        }
    }

    fun getCurrencyLabel(currency: String): String {
        return if (currency == "USD") {
            context.getString(R.string.currency_usd)
        } else {
            context.getString(R.string.currency_eur)
        }
    }

    fun getCurrencySymbol(currency: String): String {
        return if (currency == "USD") "$" else "\u20AC"
    }
}
