package com.example.moneymanager

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Handles all SharedPreferences read/write so MainActivity stays clean.
 */
class PrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson  = Gson()

    companion object {
        private const val PREFS_NAME                = "MoneyManager"
        private const val KEY_LANGUAGE              = "language"
        private const val KEY_THEME                 = "theme"
        private const val KEY_CURRENCY              = "currency"
        private const val KEY_INCOMES               = "incomes"
        private const val KEY_SUBSCRIPTIONS         = "subscriptions"
        private const val KEY_EXPENSES              = "expenses"
        private const val KEY_CATEGORIES            = "categories"
        private const val KEY_PAYMENT_ACCOUNTS      = "payment_accounts"
        private const val KEY_NEXT_PAYMENT_ACCT_ID  = "next_payment_account_id"
        private const val KEY_CASH_AMOUNT           = "cash_amount"
        const val THEME_MINIMAL                     = "minimal"
        const val THEME_HELL                        = "hell"
    }

    // ── Language ──────────────────────────────────────────────────────────────

    fun saveLanguage(code: String)  = prefs.edit().putString(KEY_LANGUAGE, code).apply()
    fun loadLanguage(): String      = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

    // ── Theme ─────────────────────────────────────────────────────────────────

    fun saveTheme(theme: String)    = prefs.edit().putString(KEY_THEME, theme).apply()
    fun loadTheme(): String         = prefs.getString(KEY_THEME, THEME_MINIMAL) ?: THEME_MINIMAL

    // ── Currency ──────────────────────────────────────────────────────────────

    fun saveCurrency(symbol: String) = prefs.edit().putString(KEY_CURRENCY, symbol).apply()
    fun loadCurrency(): String       = prefs.getString(KEY_CURRENCY, "$") ?: "$"

    // ── Cash amount ───────────────────────────────────────────────────────────

    fun saveCashAmount(amount: Double) = prefs.edit().putFloat(KEY_CASH_AMOUNT, amount.toFloat()).apply()
    fun loadCashAmount(): Double       = prefs.getFloat(KEY_CASH_AMOUNT, 0f).toDouble()

    // ── Next account ID ───────────────────────────────────────────────────────

    fun saveNextAccountId(id: Long) = prefs.edit().putLong(KEY_NEXT_PAYMENT_ACCT_ID, id).apply()
    fun loadNextAccountId(): Long   = prefs.getLong(KEY_NEXT_PAYMENT_ACCT_ID, Transaction.DEFAULT_CARD_ACCOUNT_ID + 1)

    // ── Transactions ──────────────────────────────────────────────────────────

    fun saveIncomes(list: List<Income>)             = saveJson(KEY_INCOMES, list)
    fun loadIncomes(): MutableList<Income>          = loadJson(KEY_INCOMES)

    fun saveSubscriptions(list: List<Subscription>) = saveJson(KEY_SUBSCRIPTIONS, list)
    fun loadSubscriptions(): MutableList<Subscription> = loadJson(KEY_SUBSCRIPTIONS)

    fun saveExpenses(list: List<Expense>)           = saveJson(KEY_EXPENSES, list)
    fun loadExpenses(): MutableList<Expense>        = loadJson(KEY_EXPENSES)

    fun saveCategories(list: List<String>)          = saveJson(KEY_CATEGORIES, list)
    fun loadCategories(): MutableList<String>       = loadJson(KEY_CATEGORIES)

    // ── Accounts ──────────────────────────────────────────────────────────────

    fun saveAccounts(list: List<Account>)           = saveJson(KEY_PAYMENT_ACCOUNTS, list)
    fun loadAccounts(): MutableList<Account>        = loadJson(KEY_PAYMENT_ACCOUNTS)

    // ── Generic helpers ───────────────────────────────────────────────────────

    private inline fun <reified T> saveJson(key: String, data: T) {
        prefs.edit().putString(key, gson.toJson(data)).apply()
    }

    private inline fun <reified T> loadJson(key: String): MutableList<T> {
        val json = prefs.getString(key, null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, object : TypeToken<MutableList<T>>() {}.type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}
