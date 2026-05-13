package com.example.moneymanager

import java.util.Locale

internal fun csvEscape(value: String): String {
    val normalized = value.replace("\r\n", "\n").replace('\r', '\n')
    return if (normalized.any { it == ',' || it == '"' || it == '\n' }) {
        "\"${normalized.replace("\"", "\"\"")}\""
    } else {
        normalized
    }
}

internal fun parseCsvRows(csv: String, delimiter: Char = ','): List<List<String>> {
    val rows = mutableListOf<MutableList<String>>()
    var row = mutableListOf<String>()
    val field = StringBuilder()
    var inQuotes = false
    var index = 0
    while (index < csv.length) {
        val char = csv[index]
        when {
            inQuotes && char == '"' && index + 1 < csv.length && csv[index + 1] == '"' -> {
                field.append('"')
                index++
            }
            char == '"' -> inQuotes = !inQuotes
            !inQuotes && char == delimiter -> {
                row.add(field.toString())
                field.clear()
            }
            !inQuotes && (char == '\n' || char == '\r') -> {
                if (char == '\r' && index + 1 < csv.length && csv[index + 1] == '\n') index++
                row.add(field.toString())
                field.clear()
                rows.add(row)
                row = mutableListOf()
            }
            else -> field.append(char)
        }
        index++
    }
    row.add(field.toString())
    rows.add(row)
    return rows
}

internal fun detectCsvDelimiter(csv: String): Char {
    val firstLine = csv.lineSequence().firstOrNull { it.isNotBlank() }.orEmpty()
    val commaCount = countDelimiterOutsideQuotes(firstLine, ',')
    val semicolonCount = countDelimiterOutsideQuotes(firstLine, ';')
    return if (semicolonCount > commaCount) ';' else ','
}

internal fun parseCsvAccountType(value: String): AccountType {
    return runCatching { AccountType.valueOf(value.trim().uppercase(Locale.US)) }.getOrNull()
        ?: when (value.trim().lowercase(Locale.US)) {
            "cash", "bar", "наличные" -> AccountType.CASH
            "paypal", "wallet", "e-wallet" -> AccountType.PAYPAL
            "bank_account", "bank account" -> AccountType.BANK_ACCOUNT
            "card", "bank_card", "bank card" -> AccountType.BANK_CARD
            else -> AccountType.BANK_CARD
        }
}

private fun countDelimiterOutsideQuotes(line: String, delimiter: Char): Int {
    var count = 0
    var inQuotes = false
    var index = 0
    while (index < line.length) {
        val char = line[index]
        when {
            inQuotes && char == '"' && index + 1 < line.length && line[index + 1] == '"' -> index++
            char == '"' -> inQuotes = !inQuotes
            !inQuotes && char == delimiter -> count++
        }
        index++
    }
    return count
}
