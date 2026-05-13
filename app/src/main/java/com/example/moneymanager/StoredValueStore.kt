package com.example.moneymanager

import android.content.SharedPreferences

internal class StoredValueStore(private val moneyDao: MoneyDao) {
    fun readString(prefs: SharedPreferences, key: String, defaultValue: String): String {
        return moneyDao.getValue(key) ?: prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun readDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
        return moneyDao.getValue(key)?.toDoubleOrNull() ?: readPrefsDouble(prefs, key, defaultValue)
    }

    fun readBoolean(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
        return moneyDao.getValue(key)?.toBooleanStrictOrNull() ?: readPrefsBoolean(prefs, key, defaultValue)
    }

    fun readLong(prefs: SharedPreferences, key: String, defaultValue: Long): Long {
        return moneyDao.getValue(key)?.toLongOrNull() ?: readPrefsLong(prefs, key, defaultValue)
    }

    fun storeString(prefs: SharedPreferences.Editor, key: String, value: String) {
        moneyDao.put(StoredValue(key, value, System.currentTimeMillis()))
        prefs.putString(key, value)
    }

    fun storeDouble(prefs: SharedPreferences.Editor, key: String, value: Double) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putFloat(key, value.toFloat())
    }

    fun storeBoolean(prefs: SharedPreferences.Editor, key: String, value: Boolean) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putBoolean(key, value)
    }

    fun storeLong(prefs: SharedPreferences.Editor, key: String, value: Long) {
        moneyDao.put(StoredValue(key, value.toString(), System.currentTimeMillis()))
        prefs.putLong(key, value)
    }

    fun migrateSharedPreferencesToRoomIfNeeded(prefs: SharedPreferences) {
        if (prefs.getBoolean(KEY_ROOM_MIGRATION_DONE, false)) {
            return
        }
        if (moneyDao.count() > 0) {
            prefs.edit().putBoolean(KEY_ROOM_MIGRATION_DONE, true).apply()
            return
        }

        val now = System.currentTimeMillis()
        val values = mutableListOf<StoredValue>()
        fun addString(key: String, defaultValue: String) {
            values.add(StoredValue(key, prefs.getString(key, defaultValue) ?: defaultValue, now))
        }
        fun addDouble(key: String, defaultValue: Double) {
            values.add(StoredValue(key, readPrefsDouble(prefs, key, defaultValue).toString(), now))
        }
        fun addBoolean(key: String, defaultValue: Boolean) {
            values.add(StoredValue(key, readPrefsBoolean(prefs, key, defaultValue).toString(), now))
        }
        fun addLong(key: String, defaultValue: Long) {
            values.add(StoredValue(key, readPrefsLong(prefs, key, defaultValue).toString(), now))
        }

        addString(KEY_INCOMES, "[]")
        addString(KEY_SUBSCRIPTIONS, "[]")
        addString(KEY_RECURRING_EXPENSES, "[]")
        addString(KEY_EXPENSES, "[]")
        addString(KEY_TRANSFERS, "[]")
        addString(KEY_CATEGORIES, "[]")
        addString(KEY_FAVORITE_CATEGORIES, "[]")
        addString(KEY_SAVED_TAGS, "[]")
        addString(KEY_SAVED_SUBCATEGORIES, "[]")
        addString(KEY_CATEGORY_BUDGETS, "[]")
        addString(KEY_MONTHLY_GOAL_CATEGORIES, "[]")
        addString(KEY_PAYMENT_ACCOUNTS, "[]")
        addString(KEY_ACCOUNT_BALANCES, "{}")
        addString(KEY_ACCOUNT_LABELS, "{}")
        addString(KEY_APP_LOCK_PIN_HASH, "")
        addString(KEY_APP_LOCK_PIN_SALT, "")
        addDouble(KEY_CASH_AMOUNT, 0.0)
        addDouble(KEY_MONTHLY_GOAL_AMOUNT, 0.0)
        addBoolean(KEY_MONTHLY_GOAL_INCLUDE_EXPENSES, true)
        addBoolean(KEY_MONTHLY_GOAL_INCLUDE_SUBSCRIPTIONS, true)
        addBoolean(KEY_APP_LOCK_ENABLED, false)
        addBoolean(KEY_APP_LOCK_DEVICE_AUTH, false)
        addLong(KEY_NEXT_PAYMENT_ACCOUNT_ID, DEFAULT_CARD_ACCOUNT_ID + 1L)

        moneyDao.putAll(values)
        prefs.edit().putBoolean(KEY_ROOM_MIGRATION_DONE, true).apply()
    }
}

internal fun readPrefsDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
    return runCatching { prefs.getFloat(key, defaultValue.toFloat()).toDouble() }
        .getOrElse { prefs.getString(key, defaultValue.toString())?.toDoubleOrNull() ?: defaultValue }
}

internal fun readPrefsBoolean(prefs: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
    return runCatching { prefs.getBoolean(key, defaultValue) }
        .getOrElse { prefs.getString(key, defaultValue.toString())?.toBooleanStrictOrNull() ?: defaultValue }
}

internal fun readPrefsLong(prefs: SharedPreferences, key: String, defaultValue: Long): Long {
    return runCatching { prefs.getLong(key, defaultValue) }
        .getOrElse { prefs.getString(key, defaultValue.toString())?.toLongOrNull() ?: defaultValue }
}
