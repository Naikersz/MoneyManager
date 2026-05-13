package com.example.moneymanager

import android.content.Context
import android.net.Uri
import java.io.OutputStreamWriter

internal fun Context.writeTextToUri(uri: Uri, text: String): Boolean {
    return runCatching {
        contentResolver.openOutputStream(uri)?.use { stream ->
            OutputStreamWriter(stream, Charsets.UTF_8).use { writer ->
                writer.write(text)
            }
        } ?: error("No output stream")
    }.isSuccess
}

internal fun Context.readTextFromUri(uri: Uri): String? {
    return runCatching {
        contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
            reader?.readText()
        }
    }.getOrNull()
}
