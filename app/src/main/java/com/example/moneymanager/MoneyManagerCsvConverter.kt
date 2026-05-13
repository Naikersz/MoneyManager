package com.example.moneymanager

import android.content.Context
import java.util.Calendar
import java.util.Locale

internal class MoneyManagerCsvConverter(
    private val context: Context,
    private val paymentAccountsProvider: () -> List<PaymentAccount>,
    private val getPaymentAccountName: (Long?) -> String,
    private val getPaymentAccountType: (Long?) -> AccountType,
    private val parseIsoDate: (String?) -> Calendar?,
    private val todayIsoProvider: () -> String
) {
    fun createCsvData(
        incomes: List<Income>,
        expenses: List<Expense>,
        subscriptions: List<Subscription>,
        recurringExpenses: List<RecurringExpense>,
        transfers: List<AccountTransfer>
    ): String {
        val header = listOf(
            "type",
            "name",
            "amount",
            "date",
            "period",
            "category",
            "subcategory",
            "tags",
            "account",
            "account_type",
            "reminders_enabled",
            "reminder_days_before",
            "paid_dates",
            "skipped_dates",
            "subscription_status",
            "subscription_status_date",
            "to_account",
            "to_account_type",
            "note"
        )
        val rows = mutableListOf(header)
        incomes.forEach { income ->
            rows.add(
                listOf(
                    "income",
                    income.name,
                    income.amount.toString(),
                    income.expectedDate ?: income.date,
                    income.period,
                    income.type,
                    "",
                    "",
                    getPaymentAccountName(income.accountId),
                    getPaymentAccountType(income.accountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        expenses.forEach { expense ->
            rows.add(
                listOf(
                    "expense",
                    expense.name,
                    expense.amount.toString(),
                    expense.date,
                    "",
                    expense.category.orEmpty(),
                    expense.subcategory.orEmpty(),
                    expense.tags.joinToString("|"),
                    getPaymentAccountName(expense.accountId),
                    getPaymentAccountType(expense.accountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        subscriptions.forEach { subscription ->
            rows.add(
                listOf(
                    "subscription",
                    subscription.name,
                    subscription.amount.toString(),
                    subscription.nextChargeDate.orEmpty(),
                    subscription.period,
                    "",
                    "",
                    "",
                    getPaymentAccountName(subscription.accountId),
                    getPaymentAccountType(subscription.accountId).name,
                    subscription.remindersEnabled.toString(),
                    subscription.reminderDaysBefore.toString(),
                    subscription.paidDates.joinToString("|"),
                    subscription.skippedDates.joinToString("|"),
                    normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus),
                    subscription.lifecycleDate.orEmpty(),
                    "",
                    "",
                    ""
                )
            )
        }
        recurringExpenses.forEach { recurringExpense ->
            rows.add(
                listOf(
                    "recurring_expense",
                    recurringExpense.name,
                    recurringExpense.amount.toString(),
                    recurringExpense.startDate.orEmpty(),
                    recurringExpense.period,
                    recurringExpense.category.orEmpty(),
                    recurringExpense.subcategory.orEmpty(),
                    recurringExpense.tags.joinToString("|"),
                    getPaymentAccountName(recurringExpense.accountId),
                    getPaymentAccountType(recurringExpense.accountId).name,
                    recurringExpense.remindersEnabled.toString(),
                    recurringExpense.reminderDaysBefore.toString(),
                    recurringExpense.paidDates.joinToString("|"),
                    recurringExpense.skippedDates.joinToString("|"),
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        transfers.forEach { transfer ->
            rows.add(
                listOf(
                    "transfer",
                    transfer.note.orEmpty().ifBlank { context.getString(R.string.transfer) },
                    transfer.amount.toString(),
                    transfer.date,
                    "",
                    "",
                    "",
                    "",
                    getPaymentAccountName(transfer.fromAccountId),
                    getPaymentAccountType(transfer.fromAccountId).name,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    getPaymentAccountName(transfer.toAccountId),
                    getPaymentAccountType(transfer.toAccountId).name,
                    transfer.note.orEmpty()
                )
            )
        }
        return rows.joinToString("\n") { row ->
            row.joinToString(",") { csvEscape(it) }
        }
    }

    fun parseCsvImport(csv: String): CsvImportData {
        val rows = parseCsvRows(csv, detectCsvDelimiter(csv)).filter { row -> row.any { it.isNotBlank() } }
        if (rows.size < 2) return CsvImportData()
        val headers = rows.first().map { it.trim().removePrefix("\uFEFF").lowercase(Locale.US) }
        fun rowValue(row: List<String>, name: String): String {
            val index = headers.indexOf(name)
            return if (index in row.indices) row[index].trim() else ""
        }

        val importData = CsvImportData()
        rows.drop(1).forEach { row ->
            val type = rowValue(row, "type").lowercase(Locale.US)
            val name = rowValue(row, "name").ifBlank { type.replace('_', ' ') }
            val amount = rowValue(row, "amount").replace(',', '.').toDoubleOrNull() ?: return@forEach
            val date = rowValue(row, "date")
            val period = rowValue(row, "period").lowercase(Locale.US)
                .takeIf { it == "yearly" || it == "monthly" || it == "once" }
            val category = rowValue(row, "category").takeIf { it.isNotBlank() }
            val subcategory = rowValue(row, "subcategory").takeIf { it.isNotBlank() }
            val tags = parseTags(rowValue(row, "tags"))
            val accountId = resolveCsvAccountId(rowValue(row, "account"), rowValue(row, "account_type"))
            val toAccountId = resolveCsvAccountId(rowValue(row, "to_account"), rowValue(row, "to_account_type"))
            val remindersEnabled = rowValue(row, "reminders_enabled").toBooleanStrictOrNull() ?: true
            val reminderDays = rowValue(row, "reminder_days_before").toIntOrNull()
                ?.let(::sanitizeReminderDays) ?: 1
            val paidDates = parseDateList(rowValue(row, "paid_dates"))
            val skippedDates = parseDateList(rowValue(row, "skipped_dates")) - paidDates.toSet()
            val subscriptionLifecycleStatus = normalizeSubscriptionLifecycleStatus(
                rowValue(row, "subscription_status").takeIf { it.isNotBlank() }
            )
            val subscriptionLifecycleDate = rowValue(row, "subscription_status_date")
                .takeIf { parseIsoDate(it) != null }
                .takeIf { subscriptionLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE }
                ?: todayIsoProvider().takeIf { subscriptionLifecycleStatus != SUBSCRIPTION_STATUS_ACTIVE }
            val note = normalizeOptionalText(rowValue(row, "note"))

            when (type) {
                "income" -> importData.incomes.add(
                    Income(
                        name = name.ifBlank { context.getString(R.string.income_default_name) },
                        amount = amount,
                        date = date.ifBlank { todayIsoProvider() },
                        type = category ?: context.getString(R.string.income_other),
                        expectedDate = date.takeIf { it.isNotBlank() },
                        period = if (period == "monthly") "monthly" else "once",
                        accountId = accountId
                    )
                )
                "expense" -> importData.expenses.add(
                    Expense(
                        name = name.ifBlank { category ?: context.getString(R.string.expense_default_name) },
                        amount = amount,
                        date = date.ifBlank { todayIsoProvider() },
                        category = category,
                        accountId = accountId,
                        subcategory = subcategory,
                        tags = tags
                    )
                )
                "subscription" -> importData.subscriptions.add(
                    Subscription(
                        name = name.ifBlank { context.getString(R.string.subscription) },
                        amount = amount,
                        period = if (period == "yearly") "yearly" else "monthly",
                        nextChargeDate = date.takeIf { it.isNotBlank() },
                        accountId = accountId,
                        remindersEnabled = remindersEnabled,
                        reminderDaysBefore = reminderDays,
                        lifecycleStatus = subscriptionLifecycleStatus,
                        lifecycleDate = subscriptionLifecycleDate,
                        paidDates = paidDates,
                        skippedDates = skippedDates
                    )
                )
                "recurring_expense", "recurring" -> importData.recurringExpenses.add(
                    RecurringExpense(
                        name = name.ifBlank { category ?: context.getString(R.string.recurring_expense) },
                        amount = amount,
                        category = category,
                        period = if (period == "yearly") "yearly" else "monthly",
                        startDate = date.takeIf { it.isNotBlank() },
                        accountId = accountId,
                        remindersEnabled = remindersEnabled,
                        reminderDaysBefore = reminderDays,
                        paidDates = paidDates,
                        skippedDates = skippedDates,
                        subcategory = subcategory,
                        tags = tags
                    )
                )
                "transfer" -> if (accountId != toAccountId) {
                    importData.transfers.add(
                        AccountTransfer(
                            fromAccountId = accountId,
                            toAccountId = toAccountId,
                            amount = amount,
                            date = date.ifBlank { todayIsoProvider() },
                            note = note ?: name.takeIf { it.isNotBlank() && it != "transfer" }
                        )
                    )
                }
            }
        }
        return importData
    }

    private fun resolveCsvAccountId(name: String, typeValue: String): Long {
        val safeName = name.trim()
        if (safeName.isBlank()) return DEFAULT_CASH_ACCOUNT_ID
        paymentAccountsProvider()
            .firstOrNull { it.name.equals(safeName, ignoreCase = true) }
            ?.let { return it.id }
        return paymentAccountsProvider()
            .firstOrNull { it.type == parseCsvAccountType(typeValue) }
            ?.id ?: DEFAULT_CASH_ACCOUNT_ID
    }

    private fun parseDateList(value: String): List<String> {
        return value.split("|", ";")
            .map { it.trim() }
            .filter { parseIsoDate(it) != null }
            .distinct()
    }
}
