package com.example.moneymanager.ui

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.readTextFromUri
import com.example.moneymanager.writeTextToUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ImportExportController(
    private val activity: AppCompatActivity,
    private val backupJsonProvider: () -> String,
    private val onBackupExported: (Boolean) -> Unit,
    private val onBackupImportText: (String?) -> Unit,
    private val csvProvider: () -> String,
    private val onCsvExported: (Boolean) -> Unit,
    private val onCsvImportText: (String?) -> Unit
) {
    private val exportBackupLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri?.let(::writeBackupToUri)
        }

    private val importBackupLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            onBackupImportText(uri?.let(activity::readTextFromUri))
        }

    private val exportCsvLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri?.let(::writeCsvToUri)
        }

    private val importCsvLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            onCsvImportText(uri?.let(activity::readTextFromUri))
        }

    fun exportBackup() {
        exportBackupLauncher.launch("money-manager-backup-${timestamp()}.json")
    }

    fun importBackup() {
        importBackupLauncher.launch(arrayOf("application/json", "text/json", "text/*"))
    }

    fun exportCsv() {
        exportCsvLauncher.launch("money-manager-records-${timestamp()}.csv")
    }

    fun importCsv() {
        importCsvLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "text/*"))
    }

    private fun writeBackupToUri(uri: Uri) {
        val backupJson = runCatching(backupJsonProvider).getOrNull()
        onBackupExported(backupJson?.let { activity.writeTextToUri(uri, it) } == true)
    }

    private fun writeCsvToUri(uri: Uri) {
        val csv = runCatching(csvProvider).getOrNull()
        onCsvExported(csv?.let { activity.writeTextToUri(uri, it) } == true)
    }

    private fun timestamp(): String {
        return SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
    }
}
