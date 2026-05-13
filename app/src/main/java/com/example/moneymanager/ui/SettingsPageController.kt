package com.example.moneymanager.ui

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.moneymanager.R

internal class SettingsPageController(
    private val context: Context,
    private val root: View,
    private val getLanguageLabel: () -> String,
    private val getThemeLabel: () -> String,
    private val getCurrencyLabel: () -> String,
    private val getAccountsSummary: () -> String,
    private val isAppLockEnabled: () -> Boolean,
    private val getTagsCount: () -> Int,
    private val getSubcategoriesCount: () -> Int,
    private val onLanguageClick: () -> Unit,
    private val onThemeClick: () -> Unit,
    private val onCurrencyClick: () -> Unit,
    private val onAccountsClick: () -> Unit,
    private val onAppLockClick: () -> Unit,
    private val onTagsClick: () -> Unit,
    private val onExportBackupClick: () -> Unit,
    private val onImportBackupClick: () -> Unit,
    private val onExportCsvClick: () -> Unit,
    private val onImportCsvClick: () -> Unit
) {
    private lateinit var tvLanguageValue: TextView
    private lateinit var tvThemeValue: TextView
    private lateinit var tvCurrencyValue: TextView
    private lateinit var tvAccountsValue: TextView
    private lateinit var tvAppLockValue: TextView
    private lateinit var tvTagsValue: TextView

    fun bind() {
        tvLanguageValue = root.findViewById(R.id.tvPageLanguageValue)
        tvThemeValue = root.findViewById(R.id.tvPageThemeValue)
        tvCurrencyValue = root.findViewById(R.id.tvPageCurrencyValue)
        tvAccountsValue = root.findViewById(R.id.tvPageAccountsValue)
        tvAppLockValue = root.findViewById(R.id.tvPageAppLockValue)
        tvTagsValue = root.findViewById(R.id.tvPageTagsValue)
        root.findViewById<View>(R.id.rowPageLanguage).setOnClickListener { onLanguageClick() }
        root.findViewById<View>(R.id.rowPageTheme).setOnClickListener { onThemeClick() }
        root.findViewById<View>(R.id.rowPageCurrency).setOnClickListener { onCurrencyClick() }
        root.findViewById<View>(R.id.rowPageAccounts).setOnClickListener { onAccountsClick() }
        root.findViewById<View>(R.id.rowPageAppLock).setOnClickListener { onAppLockClick() }
        root.findViewById<View>(R.id.rowPageTags).setOnClickListener { onTagsClick() }
        root.findViewById<View>(R.id.rowPageExportBackup).setOnClickListener { onExportBackupClick() }
        root.findViewById<View>(R.id.rowPageImportBackup).setOnClickListener { onImportBackupClick() }
        root.findViewById<View>(R.id.rowPageExportCsv).setOnClickListener { onExportCsvClick() }
        root.findViewById<View>(R.id.rowPageImportCsv).setOnClickListener { onImportCsvClick() }
    }

    fun updateValues() {
        tvLanguageValue.text = getLanguageLabel()
        tvThemeValue.text = getThemeLabel()
        tvCurrencyValue.text = getCurrencyLabel()
        tvAccountsValue.text = getAccountsSummary()
        tvAppLockValue.text = context.getString(
            if (isAppLockEnabled()) R.string.enabled else R.string.disabled
        )
        tvTagsValue.text = context.getString(
            R.string.tags_summary,
            getTagsCount(),
            getSubcategoriesCount()
        )
    }
}
