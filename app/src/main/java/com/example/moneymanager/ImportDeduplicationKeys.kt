package com.example.moneymanager

import java.util.Locale

internal fun incomeImportKey(income: Income): String {
    return listOf(
        normalizeImportKeyPart(income.name),
        importAmountKey(income.amount),
        normalizeImportKeyPart(income.expectedDate ?: income.date),
        normalizeImportKeyPart(income.type),
        normalizeImportKeyPart(income.period),
        income.accountId.toString()
    ).joinToString("|")
}

internal fun subscriptionImportKey(subscription: Subscription): String {
    return listOf(
        normalizeImportKeyPart(subscription.name),
        importAmountKey(subscription.amount),
        normalizeImportKeyPart(subscription.period),
        normalizeImportKeyPart(subscription.nextChargeDate),
        subscription.accountId.toString()
    ).joinToString("|")
}

internal fun recurringExpenseImportKey(recurringExpense: RecurringExpense): String {
    return listOf(
        normalizeImportKeyPart(recurringExpense.name),
        importAmountKey(recurringExpense.amount),
        normalizeImportKeyPart(recurringExpense.category),
        normalizeImportKeyPart(recurringExpense.subcategory),
        normalizeImportKeyPart(recurringExpense.period),
        normalizeImportKeyPart(recurringExpense.startDate),
        recurringExpense.accountId.toString()
    ).joinToString("|")
}

internal fun expenseImportKey(expense: Expense): String {
    return listOf(
        normalizeImportKeyPart(expense.name),
        importAmountKey(expense.amount),
        normalizeImportKeyPart(expense.date),
        normalizeImportKeyPart(expense.category),
        normalizeImportKeyPart(expense.subcategory),
        expense.accountId.toString()
    ).joinToString("|")
}

internal fun transferImportKey(transfer: AccountTransfer): String {
    return listOf(
        transfer.fromAccountId.toString(),
        transfer.toAccountId.toString(),
        importAmountKey(transfer.amount),
        normalizeImportKeyPart(transfer.date),
        normalizeImportKeyPart(transfer.note)
    ).joinToString("|")
}

private fun normalizeImportKeyPart(value: String?): String {
    return value.orEmpty().trim().lowercase(Locale.getDefault())
}

private fun importAmountKey(value: Double): String {
    return String.format(Locale.US, "%.4f", value)
}
