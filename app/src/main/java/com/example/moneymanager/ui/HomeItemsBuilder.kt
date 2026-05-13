package com.example.moneymanager.ui

import android.content.Context
import com.example.moneymanager.AccountTransfer
import com.example.moneymanager.Expense
import com.example.moneymanager.Income
import com.example.moneymanager.Item
import com.example.moneymanager.ItemType
import com.example.moneymanager.R
import com.example.moneymanager.RecurringExpense
import com.example.moneymanager.SUBSCRIPTION_STATUS_ACTIVE
import com.example.moneymanager.Subscription
import com.example.moneymanager.appendTags
import com.example.moneymanager.isPaymentPaid
import com.example.moneymanager.isPaymentSkipped
import com.example.moneymanager.normalizeSubscriptionLifecycleStatus
import java.util.Calendar

internal class HomeItemsBuilder(
    private val context: Context,
    private val incomesProvider: () -> List<Income>,
    private val subscriptionsProvider: () -> List<Subscription>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val expensesProvider: () -> List<Expense>,
    private val transfersProvider: () -> List<AccountTransfer>,
    private val displayedMonthProvider: () -> Calendar,
    private val selectedAccountProvider: () -> String,
    private val getIncomeOccurrenceDateInMonth: (Income, Calendar) -> String?,
    private val getSubscriptionOccurrenceDateInMonth: (Subscription, Calendar) -> String?,
    private val getRecurringExpenseOccurrenceDateInMonth: (RecurringExpense, Calendar) -> String?,
    private val getNextSubscriptionDate: (Subscription) -> String?,
    private val getSubscriptionLifecycleMeta: (Subscription) -> String,
    private val formatDisplayDate: (String?) -> String,
    private val getIncomeTypeDisplayName: (String) -> String,
    private val buildCategoryLabel: (String?, String?) -> String,
    private val getPaymentAccountIcon: (Long?) -> String,
    private val getPaymentAccountName: (Long?) -> String,
    private val getTransferDeltaForFilter: (AccountTransfer, String?) -> Double
) {
    fun build(): List<Item> {
        val displayedMonth = displayedMonthProvider()
        val selectedAccount = selectedAccountProvider()
        return mutableListOf<Item>().apply {
            addIncomes(displayedMonth)
            addSubscriptions(displayedMonth)
            addRecurringExpenses(displayedMonth)
            addExpenses()
            addTransfers(selectedAccount)
        }
    }

    private fun MutableList<Item>.addIncomes(displayedMonth: Calendar) {
        incomesProvider().forEachIndexed { index, income ->
            val trailingValue = if (income.period == "monthly") {
                getIncomeOccurrenceDateInMonth(income, displayedMonth)
                    ?.let(formatDisplayDate)
                    ?: income.expectedDate?.let(formatDisplayDate)
                    ?: income.date
            } else {
                income.expectedDate?.let(formatDisplayDate) ?: income.date
            }
            val periodLabel = if (income.period == "monthly") {
                context.getString(R.string.monthly)
            } else {
                context.getString(R.string.once)
            }
            val typeLabel = income.type.takeIf { it.isNotBlank() }
                ?.let(getIncomeTypeDisplayName)
                ?: context.getString(R.string.income)
            add(
                Item(
                    name = income.name,
                    amount = income.amount,
                    meta = "$typeLabel \u00B7 $periodLabel",
                    trailing = "${getPaymentAccountIcon(income.accountId)} ${getPaymentAccountName(income.accountId)} \u00B7 $trailingValue",
                    type = ItemType.INCOME,
                    sourceIndex = index
                )
            )
        }
    }

    private fun MutableList<Item>.addSubscriptions(displayedMonth: Calendar) {
        subscriptionsProvider().forEachIndexed { index, subscription ->
            val amount = subscription.amount
            val periodLabel = if (subscription.period == "monthly") {
                context.getString(R.string.monthly)
            } else {
                context.getString(R.string.yearly)
            }
            val lifecycleStatus = normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus)
            val occurrenceDate = getSubscriptionOccurrenceDateInMonth(subscription, displayedMonth)
            val lifecycleMeta = getSubscriptionLifecycleMeta(subscription)
            val nextChargeDisplay = if (lifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) {
                occurrenceDate?.let(formatDisplayDate)
                    ?: getNextSubscriptionDate(subscription)?.let(formatDisplayDate)
                    ?: periodLabel
            } else {
                lifecycleMeta
            }
            add(
                Item(
                    name = subscription.name,
                    amount = if (lifecycleStatus == SUBSCRIPTION_STATUS_ACTIVE) -amount else 0.0,
                    meta = "$periodLabel \u00B7 $nextChargeDisplay",
                    trailing = "${getPaymentAccountIcon(subscription.accountId)} ${getPaymentAccountName(subscription.accountId)}",
                    type = ItemType.SUBSCRIPTION,
                    sourceIndex = index
                )
            )
        }
    }

    private fun MutableList<Item>.addRecurringExpenses(displayedMonth: Calendar) {
        recurringExpensesProvider().forEachIndexed { index, recurringExpense ->
            val categoryLabel = buildCategoryLabel(recurringExpense.category, recurringExpense.subcategory)
            val periodLabel = if (recurringExpense.period == "yearly") {
                context.getString(R.string.yearly)
            } else {
                context.getString(R.string.monthly)
            }
            val occurrenceDate = getRecurringExpenseOccurrenceDateInMonth(recurringExpense, displayedMonth)
            val isSkipped = isPaymentSkipped(recurringExpense.skippedDates, occurrenceDate)
            val isPaid = isPaymentPaid(recurringExpense.paidDates, occurrenceDate)
            val statusLabel = when {
                isSkipped -> " \u00B7 ${context.getString(R.string.payment_status_skipped)}"
                isPaid -> " \u00B7 ${context.getString(R.string.payment_status_paid)}"
                else -> ""
            }
            val occurrenceDisplay = occurrenceDate?.let(formatDisplayDate)
                ?: recurringExpense.startDate?.let(formatDisplayDate)
                ?: periodLabel
            add(
                Item(
                    name = if (recurringExpense.favorite) {
                        "${context.getString(R.string.favorite_marker)} ${recurringExpense.name}"
                    } else {
                        recurringExpense.name
                    },
                    amount = if (isSkipped) 0.0 else -recurringExpense.amount,
                    meta = appendTags("$categoryLabel \u00B7 $periodLabel$statusLabel", recurringExpense.tags),
                    trailing = "${getPaymentAccountIcon(recurringExpense.accountId)} ${getPaymentAccountName(recurringExpense.accountId)} \u00B7 $occurrenceDisplay",
                    type = ItemType.RECURRING_EXPENSE,
                    sourceIndex = index
                )
            )
        }
    }

    private fun MutableList<Item>.addExpenses() {
        expensesProvider().forEachIndexed { index, expense ->
            val categoryLabel = buildCategoryLabel(expense.category, expense.subcategory)
            add(
                Item(
                    name = expense.name,
                    amount = -expense.amount,
                    meta = appendTags(categoryLabel, expense.tags),
                    trailing = "${getPaymentAccountIcon(expense.accountId)} ${getPaymentAccountName(expense.accountId)} \u00B7 ${formatDisplayDate(expense.date)}",
                    type = ItemType.EXPENSE,
                    sourceIndex = index
                )
            )
        }
    }

    private fun MutableList<Item>.addTransfers(selectedAccount: String) {
        transfersProvider().forEachIndexed { index, transfer ->
            add(
                Item(
                    name = transfer.note ?: context.getString(R.string.transfer),
                    amount = getTransferDeltaForFilter(transfer, selectedAccount),
                    meta = context.getString(
                        R.string.transfer_route,
                        getPaymentAccountName(transfer.fromAccountId),
                        getPaymentAccountName(transfer.toAccountId)
                    ),
                    trailing = formatDisplayDate(transfer.date),
                    type = ItemType.TRANSFER,
                    sourceIndex = index
                )
            )
        }
    }
}
