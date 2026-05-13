package com.example.moneymanager

import android.content.SharedPreferences

internal fun StoredValueStore.isAppLockEnabled(prefs: SharedPreferences): Boolean {
    return readBoolean(prefs, KEY_APP_LOCK_ENABLED, false) && getStoredPinHash(prefs).isNotBlank()
}

internal fun StoredValueStore.isDeviceAuthEnabled(prefs: SharedPreferences): Boolean {
    return readBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, false)
}

internal fun StoredValueStore.getStoredPinHash(prefs: SharedPreferences): String {
    return readString(prefs, KEY_APP_LOCK_PIN_HASH, "")
}

internal fun StoredValueStore.getStoredPinSalt(prefs: SharedPreferences): String {
    return readString(prefs, KEY_APP_LOCK_PIN_SALT, "")
}

internal fun StoredValueStore.setAppLockValues(
    prefs: SharedPreferences.Editor,
    enabled: Boolean,
    pinHash: String,
    pinSalt: String,
    deviceAuth: Boolean
) {
    storeBoolean(prefs, KEY_APP_LOCK_ENABLED, enabled)
    storeString(prefs, KEY_APP_LOCK_PIN_HASH, pinHash)
    storeString(prefs, KEY_APP_LOCK_PIN_SALT, pinSalt)
    storeBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, deviceAuth)
}

internal fun StoredValueStore.clearAppLock(prefs: SharedPreferences.Editor) {
    storeBoolean(prefs, KEY_APP_LOCK_ENABLED, false)
    storeString(prefs, KEY_APP_LOCK_PIN_HASH, "")
    storeString(prefs, KEY_APP_LOCK_PIN_SALT, "")
    storeBoolean(prefs, KEY_APP_LOCK_DEVICE_AUTH, false)
}
