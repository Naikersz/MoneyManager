package com.example.moneymanager.ui

import android.content.Context
import com.example.moneymanager.Item
import com.example.moneymanager.ItemType
import com.example.moneymanager.R
import com.example.moneymanager.TAB_EXPENSES
import com.example.moneymanager.TAB_INCOME
import com.example.moneymanager.TAB_SUBSCRIPTIONS

internal class HomeItemActionsController(
    private val context: Context,
    private val onIncomeClick: (Int) -> Unit,
    private val onSubscriptionClick: (Int) -> Unit,
    private val onRecurringExpenseClick: (Int) -> Unit,
    private val onExpenseClick: (Int) -> Unit,
    private val onTransferClick: (Int) -> Unit
) {
    fun getTabTitle(position: Int): String {
        return when (position) {
            TAB_INCOME -> context.getString(R.string.income_items)
            TAB_SUBSCRIPTIONS -> context.getString(R.string.subscriptions)
            TAB_EXPENSES -> context.getString(R.string.expenses)
            else -> context.getString(R.string.all_items)
        }
    }

    fun openItemEditor(item: Item) {
        when (item.type) {
            ItemType.INCOME -> onIncomeClick(item.sourceIndex)
            ItemType.SUBSCRIPTION -> onSubscriptionClick(item.sourceIndex)
            ItemType.RECURRING_EXPENSE -> onRecurringExpenseClick(item.sourceIndex)
            ItemType.EXPENSE -> onExpenseClick(item.sourceIndex)
            ItemType.TRANSFER -> onTransferClick(item.sourceIndex)
        }
    }
}
