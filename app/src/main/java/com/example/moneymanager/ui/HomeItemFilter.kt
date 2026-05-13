package com.example.moneymanager.ui

import com.example.moneymanager.AccountTransfer
import com.example.moneymanager.AccountType
import com.example.moneymanager.Expense
import com.example.moneymanager.Income
import com.example.moneymanager.Item
import com.example.moneymanager.ItemType
import com.example.moneymanager.RecurringExpense
import com.example.moneymanager.Subscription
import com.example.moneymanager.TAB_EXPENSES
import com.example.moneymanager.TAB_INCOME
import com.example.moneymanager.TAB_SUBSCRIPTIONS
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

internal class HomeItemFilter(
    private val itemsProvider: () -> List<Item>,
    private val incomesProvider: () -> List<Income>,
    private val subscriptionsProvider: () -> List<Subscription>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val expensesProvider: () -> List<Expense>,
    private val transfersProvider: () -> List<AccountTransfer>,
    private val selectedAccountProvider: () -> String,
    private val displayedMonthProvider: () -> Calendar,
    private val searchQueryProvider: () -> String,
    private val formatAmount: (Double) -> String,
    private val formatSignedAmount: (Double) -> String,
    private val getSubscriptionLifecycleLabel: (String?) -> String,
    private val getPaymentAccountName: (Long?) -> String,
    private val getPaymentAccountType: (Long?) -> AccountType,
    private val getAccountTypeLabel: (AccountType) -> String,
    private val matchesAccountFilter: (Long, String?) -> Boolean,
    private val matchesTransferAccountFilter: (AccountTransfer, String?) -> Boolean,
    private val monthContainsIncomeOccurrence: (Income, Calendar) -> Boolean,
    private val monthContainsSubscriptionOccurrence: (Subscription, Calendar) -> Boolean,
    private val monthContainsRecurringExpenseOccurrence: (RecurringExpense, Calendar) -> Boolean,
    private val parseIsoDate: (String?) -> Calendar?,
    private val isSameMonth: (Calendar?, Calendar) -> Boolean
) {
    fun getFilteredItems(position: Int): List<Item> {
        val visibleItems = getHomeSearchFilteredItems()
        return when (position) {
            TAB_INCOME -> visibleItems.filter { it.type == ItemType.INCOME }
            TAB_SUBSCRIPTIONS -> visibleItems.filter { it.type == ItemType.SUBSCRIPTION }
            TAB_EXPENSES -> visibleItems.filter {
                it.type == ItemType.EXPENSE || it.type == ItemType.RECURRING_EXPENSE
            }
            else -> visibleItems
        }
    }

    fun getHomeVisibleItems(): List<Item> {
        return itemsProvider().filter(::matchesHomeItemFilter)
    }

    private fun getHomeSearchFilteredItems(): List<Item> {
        return getHomeVisibleItems().filter(::matchesHomeSearchFilter)
    }

    private fun matchesHomeSearchFilter(item: Item): Boolean {
        val query = searchQueryProvider().lowercase(Locale.getDefault())
        if (query.isBlank()) return true

        val searchableText = listOf(
            item.name,
            item.meta,
            item.trailing,
            formatAmount(abs(item.amount)),
            formatSignedAmount(item.amount)
        )
            .plus(getHomeItemSearchExtras(item))
            .joinToString(" ")
            .lowercase(Locale.getDefault())

        return searchableText.contains(query)
    }

    private fun getHomeItemSearchExtras(item: Item): List<String> {
        return when (item.type) {
            ItemType.INCOME -> {
                val income = incomesProvider().getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(income.accountId) + listOf(
                    income.date,
                    income.expectedDate.orEmpty(),
                    income.type
                )
            }
            ItemType.SUBSCRIPTION -> {
                val subscription = subscriptionsProvider().getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(subscription.accountId) + listOf(
                    subscription.period,
                    subscription.nextChargeDate.orEmpty(),
                    getSubscriptionLifecycleLabel(subscription.lifecycleStatus),
                    subscription.lifecycleDate.orEmpty()
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpense = recurringExpensesProvider().getOrNull(item.sourceIndex)
                    ?: return emptyList()
                getAccountSearchLabels(recurringExpense.accountId) + listOf(
                    recurringExpense.period,
                    recurringExpense.startDate.orEmpty(),
                    recurringExpense.category.orEmpty(),
                    recurringExpense.subcategory.orEmpty()
                ) + recurringExpense.tags
            }
            ItemType.EXPENSE -> {
                val expense = expensesProvider().getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(expense.accountId) + listOf(
                    expense.date,
                    expense.category.orEmpty(),
                    expense.subcategory.orEmpty()
                ) + expense.tags
            }
            ItemType.TRANSFER -> {
                val transfer = transfersProvider().getOrNull(item.sourceIndex) ?: return emptyList()
                getAccountSearchLabels(transfer.fromAccountId) +
                    getAccountSearchLabels(transfer.toAccountId) +
                    listOf(transfer.date, transfer.note.orEmpty())
            }
        }
    }

    private fun getAccountSearchLabels(accountId: Long): List<String> {
        val type = getPaymentAccountType(accountId)
        return listOf(
            getPaymentAccountName(accountId),
            getAccountTypeLabel(type),
            type.name
        )
    }

    private fun matchesHomeItemFilter(item: Item): Boolean {
        val selectedAccount = selectedAccountProvider()
        val displayedMonth = displayedMonthProvider()
        return when (item.type) {
            ItemType.INCOME -> {
                val income = incomesProvider().getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(income.accountId, selectedAccount)) return false
                if (income.period == "monthly") {
                    monthContainsIncomeOccurrence(income, displayedMonth)
                } else {
                    val date = parseIsoDate(income.expectedDate ?: income.date) ?: return true
                    isSameMonth(date, displayedMonth)
                }
            }
            ItemType.SUBSCRIPTION -> {
                val subscription = subscriptionsProvider().getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(subscription.accountId, selectedAccount)) return false
                if (subscription.nextChargeDate.isNullOrBlank()) {
                    true
                } else {
                    monthContainsSubscriptionOccurrence(subscription, displayedMonth)
                }
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpense = recurringExpensesProvider().getOrNull(item.sourceIndex)
                    ?: return false
                if (!matchesAccountFilter(recurringExpense.accountId, selectedAccount)) return false
                if (recurringExpense.startDate.isNullOrBlank()) {
                    true
                } else {
                    monthContainsRecurringExpenseOccurrence(recurringExpense, displayedMonth)
                }
            }
            ItemType.EXPENSE -> {
                val expense = expensesProvider().getOrNull(item.sourceIndex) ?: return false
                if (!matchesAccountFilter(expense.accountId, selectedAccount)) return false
                val date = parseIsoDate(expense.date) ?: return false
                isSameMonth(date, displayedMonth)
            }
            ItemType.TRANSFER -> {
                val transfer = transfersProvider().getOrNull(item.sourceIndex) ?: return false
                if (!matchesTransferAccountFilter(transfer, selectedAccount)) return false
                val date = parseIsoDate(transfer.date) ?: return false
                isSameMonth(date, displayedMonth)
            }
        }
    }
}
