package com.example.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.moneymanager.R
import com.example.moneymanager.THEME_HELL
import com.example.moneymanager.THEME_MINIMAL
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class SettingsDialogsController(
    private val context: Context,
    private val getLanguageLabel: () -> String,
    private val getThemeLabel: () -> String,
    private val getSavedLanguageCode: () -> String,
    private val saveLanguageCode: (String) -> Unit,
    private val applyLocale: (String) -> Unit,
    private val getSavedTheme: () -> String,
    private val updateTheme: (String) -> Unit,
    private val getSavedCurrency: () -> String,
    private val updateCurrency: (String) -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null)
        val tvLanguageValue = dialogView.findViewById<TextView>(R.id.tvLanguageValue)
        val tvThemeValue = dialogView.findViewById<TextView>(R.id.tvThemeValue)
        val rowLanguage = dialogView.findViewById<View>(R.id.rowLanguage)
        val rowTheme = dialogView.findViewById<View>(R.id.rowTheme)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseSettings)

        tvLanguageValue.text = getLanguageLabel()
        tvThemeValue.text = getThemeLabel()

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        rowLanguage.setOnClickListener {
            dialog.dismiss()
            showLanguagePickerDialog()
        }
        rowTheme.setOnClickListener {
            dialog.dismiss()
            showThemePickerDialog()
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showLanguagePickerDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_language_picker, null)
        val rowEnglish = dialogView.findViewById<View>(R.id.rowEnglish)
        val rowRussian = dialogView.findViewById<View>(R.id.rowRussian)
        val rowGerman = dialogView.findViewById<View>(R.id.rowGerman)
        val tvEnglishState = dialogView.findViewById<TextView>(R.id.tvEnglishState)
        val tvRussianState = dialogView.findViewById<TextView>(R.id.tvRussianState)
        val tvGermanState = dialogView.findViewById<TextView>(R.id.tvGermanState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseLanguagePicker)

        val currentLanguageCode = getSavedLanguageCode()
        tvEnglishState.text = if (currentLanguageCode == "en") context.getString(R.string.selected) else ""
        tvRussianState.text = if (currentLanguageCode == "ru") context.getString(R.string.selected) else ""
        tvGermanState.text = if (currentLanguageCode == "de") context.getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        rowEnglish.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "en") showLanguageChangeDialog("en")
        }
        rowRussian.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "ru") showLanguageChangeDialog("ru")
        }
        rowGerman.setOnClickListener {
            dialog.dismiss()
            if (currentLanguageCode != "de") showLanguageChangeDialog("de")
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showThemePickerDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_theme_picker, null)
        val rowLight = dialogView.findViewById<View>(R.id.rowThemeLight)
        val rowDark = dialogView.findViewById<View>(R.id.rowThemeDark)
        val tvLightState = dialogView.findViewById<TextView>(R.id.tvThemeLightState)
        val tvDarkState = dialogView.findViewById<TextView>(R.id.tvThemeDarkState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseThemePicker)

        val currentTheme = getSavedTheme()
        tvLightState.text = if (currentTheme == THEME_MINIMAL) context.getString(R.string.selected) else ""
        tvDarkState.text = if (currentTheme == THEME_HELL) context.getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        rowLight.setOnClickListener {
            dialog.dismiss()
            updateTheme(THEME_MINIMAL)
        }
        rowDark.setOnClickListener {
            dialog.dismiss()
            updateTheme(THEME_HELL)
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showCurrencyPickerDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_theme_picker, null)
        val rowPrimary = dialogView.findViewById<View>(R.id.rowThemeLight)
        val rowSecondary = dialogView.findViewById<View>(R.id.rowThemeDark)
        val tvPrimaryState = dialogView.findViewById<TextView>(R.id.tvThemeLightState)
        val tvSecondaryState = dialogView.findViewById<TextView>(R.id.tvThemeDarkState)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseThemePicker)
        dialogView.findViewById<TextView>(R.id.tvThemePickerTitle).text = context.getString(R.string.settings_currency)
        dialogView.findViewById<TextView>(R.id.tvThemeLightLabel).text = context.getString(R.string.currency_eur)
        dialogView.findViewById<TextView>(R.id.tvThemeDarkLabel).text = context.getString(R.string.currency_usd)

        val currentCurrency = getSavedCurrency()
        tvPrimaryState.text = if (currentCurrency == "EUR") context.getString(R.string.selected) else ""
        tvSecondaryState.text = if (currentCurrency == "USD") context.getString(R.string.selected) else ""

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        rowPrimary.setOnClickListener {
            dialog.dismiss()
            updateCurrency("EUR")
        }
        rowSecondary.setOnClickListener {
            dialog.dismiss()
            updateCurrency("USD")
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showLanguageChangeDialog(newLanguageCode: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_language_confirm, null)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelLanguageChange)
        val btnApply = dialogView.findViewById<Button>(R.id.btnApplyLanguageChange)

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnApply.setOnClickListener {
            dialog.dismiss()
            saveLanguageCode(newLanguageCode)
            applyLocale(newLanguageCode)
        }

        dialog.show()
        styleBottomSheet(dialog)
    }
}
