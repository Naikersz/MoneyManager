package com.example.moneymanager

import java.util.Locale

internal fun normalizeOptionalText(value: String?): String? {
    return value?.trim()?.takeIf { it.isNotBlank() }
}

internal fun parseTags(value: String?): List<String> {
    return value.orEmpty()
        .split(",", "|", ";")
        .map { it.trim().removePrefix("#") }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase(Locale.getDefault()) }
}

internal fun tagsToInput(tags: List<String>): String {
    return tags.joinToString(", ")
}

internal fun formatTags(tags: List<String>): String {
    return tags.joinToString(" ") { "#$it" }
}

internal fun appendTags(text: String, tags: List<String>): String {
    val tagText = formatTags(tags)
    return if (tagText.isBlank()) text else "$text \u00B7 $tagText"
}
