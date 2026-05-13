package com.example.moneymanager.ui

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.moneymanager.R
import com.example.moneymanager.generatePinSalt
import com.example.moneymanager.hashPin
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class AppLockDialogController(
    private val context: Context,
    private val isUnlocked: () -> Boolean,
    private val setUnlocked: (Boolean) -> Unit,
    private val isUnlockDialogShowing: () -> Boolean,
    private val setUnlockDialogShowing: (Boolean) -> Unit,
    private val isAppLockEnabled: () -> Boolean,
    private val isDeviceAuthEnabled: () -> Boolean,
    private val isDeviceAuthAvailable: () -> Boolean,
    private val isStoredPinValid: (String) -> Boolean,
    private val getStoredPinHash: () -> String,
    private val getStoredPinSalt: () -> String,
    private val setAppLockValues: (Boolean, String, String, Boolean) -> Unit,
    private val clearAppLock: () -> Unit,
    private val launchDeviceAuth: () -> Unit,
    private val onSettingsChanged: () -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_app_lock, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAppLockTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvAppLockMessage)
        val etCurrentPin = dialogView.findViewById<EditText>(R.id.etCurrentPin)
        val etNewPin = dialogView.findViewById<EditText>(R.id.etNewPin)
        val cbDeviceAuth = dialogView.findViewById<CheckBox>(R.id.cbDeviceAuth)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAppLock)
        val btnDisable = dialogView.findViewById<Button>(R.id.btnDisableAppLock)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveAppLock)
        val lockEnabled = isAppLockEnabled()
        val deviceAuthAvailable = isDeviceAuthAvailable()

        tvTitle.text = context.getString(R.string.app_lock_title)
        tvMessage.text = context.getString(R.string.app_lock_hint)
        etCurrentPin.visibility = if (lockEnabled) View.VISIBLE else View.GONE
        etNewPin.hint = if (lockEnabled) {
            context.getString(R.string.new_pin_optional_hint)
        } else {
            context.getString(R.string.new_pin_hint)
        }
        cbDeviceAuth.isEnabled = deviceAuthAvailable
        cbDeviceAuth.isChecked = lockEnabled && isDeviceAuthEnabled() && deviceAuthAvailable
        btnDisable.visibility = if (lockEnabled) View.VISIBLE else View.GONE

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnDisable.setOnClickListener {
            if (!isStoredPinValid(etCurrentPin.text.toString())) {
                Toast.makeText(context, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            clearAppLock()
            setUnlocked(false)
            onSettingsChanged()
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val currentPin = etCurrentPin.text.toString()
            val newPin = etNewPin.text.toString()
            if (lockEnabled && !isStoredPinValid(currentPin)) {
                Toast.makeText(context, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPin.isBlank() && !lockEnabled) {
                Toast.makeText(context, R.string.pin_too_short, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPin.isNotBlank() && newPin.length < 4) {
                Toast.makeText(context, R.string.pin_too_short, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salt = if (newPin.isNotBlank()) generatePinSalt() else getStoredPinSalt()
            val hash = if (newPin.isNotBlank()) hashPin(newPin, salt) else getStoredPinHash()
            setAppLockValues(
                true,
                hash,
                salt,
                cbDeviceAuth.isChecked && deviceAuthAvailable
            )
            setUnlocked(true)
            onSettingsChanged()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showUnlockDialog() {
        if (isUnlocked() || isUnlockDialogShowing()) return
        setUnlockDialogShowing(true)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_app_lock, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAppLockTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvAppLockMessage)
        val etCurrentPin = dialogView.findViewById<EditText>(R.id.etCurrentPin)
        val etNewPin = dialogView.findViewById<EditText>(R.id.etNewPin)
        val cbDeviceAuth = dialogView.findViewById<CheckBox>(R.id.cbDeviceAuth)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAppLock)
        val btnDeviceAuth = dialogView.findViewById<Button>(R.id.btnDisableAppLock)
        val btnUnlock = dialogView.findViewById<Button>(R.id.btnSaveAppLock)
        val canUseDeviceAuth = isDeviceAuthEnabled() && isDeviceAuthAvailable()

        tvTitle.text = context.getString(R.string.app_lock_unlock_title)
        tvMessage.text = context.getString(R.string.app_lock_unlock_message)
        etCurrentPin.hint = context.getString(R.string.current_pin_hint)
        etCurrentPin.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        etNewPin.visibility = View.GONE
        cbDeviceAuth.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnDeviceAuth.visibility = if (canUseDeviceAuth) View.VISIBLE else View.GONE
        btnDeviceAuth.text = context.getString(R.string.device_auth_unlock)
        btnUnlock.text = context.getString(R.string.app_lock_unlock)

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        btnDeviceAuth.setOnClickListener {
            launchDeviceAuth()
            dialog.dismiss()
            setUnlockDialogShowing(false)
        }
        btnUnlock.setOnClickListener {
            if (isStoredPinValid(etCurrentPin.text.toString())) {
                setUnlocked(true)
                setUnlockDialogShowing(false)
                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.pin_invalid, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setOnDismissListener {
            if (!isUnlocked()) setUnlockDialogShowing(false)
        }

        dialog.show()
        styleBottomSheet(dialog)
    }
}
