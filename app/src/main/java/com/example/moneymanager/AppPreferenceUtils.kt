package com.example.moneymanager

import android.content.Context

internal fun Context.getSavedLanguageCode(): String {
    return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, "en") ?: "en"
}

internal fun Context.saveLanguageCode(languageCode: String) {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_LANGUAGE, languageCode)
        .apply()
}

internal fun Context.getSavedTheme(): String {
    return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_THEME, THEME_MINIMAL) ?: THEME_MINIMAL
}

internal fun Context.getSavedCurrency(): String {
    return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_CURRENCY, "EUR") ?: "EUR"
}

internal fun Context.saveTheme(theme: String) {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_THEME, theme)
        .apply()
}

internal fun Context.saveCurrency(currency: String) {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_CURRENCY, currency)
        .apply()
}
