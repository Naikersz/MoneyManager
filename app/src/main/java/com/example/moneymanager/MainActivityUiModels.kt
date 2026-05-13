package com.example.moneymanager

import kotlin.math.abs

data class CategoryExpenseTotal(
    val category: String,
    val amount: Double,
    val previousAmount: Double,
    val color: Int
) {
    val delta: Double
        get() = amount - previousAmount
}

data class MonthlyTrendDetail(
    val label: String,
    val income: Double,
    val expenses: Double,
    val subscriptions: Double,
    val recurring: Double,
    val outgoing: Double,
    val net: Double
) {
    val hasMoney: Boolean
        get() = abs(income) >= 0.005 ||
            abs(expenses) >= 0.005 ||
            abs(subscriptions) >= 0.005 ||
            abs(recurring) >= 0.005 ||
            abs(net) >= 0.005
}

data class AccountActivityLine(
    val title: String,
    val meta: String,
    val amount: Double,
    val date: String
)

data class CategoryBudgetStatus(
    val budget: CategoryBudget,
    val spent: Double
) {
    val ratio: Double
        get() = if (budget.amount > 0.0) spent / budget.amount else 0.0

    val remaining: Double
        get() = budget.amount - spent

    val isOver: Boolean
        get() = remaining < -0.005
}

data class CsvImportData(
    val incomes: MutableList<Income> = mutableListOf(),
    val subscriptions: MutableList<Subscription> = mutableListOf(),
    val recurringExpenses: MutableList<RecurringExpense> = mutableListOf(),
    val expenses: MutableList<Expense> = mutableListOf(),
    val transfers: MutableList<AccountTransfer> = mutableListOf()
) {
    fun isEmpty(): Boolean {
        return incomes.isEmpty() &&
            subscriptions.isEmpty() &&
            recurringExpenses.isEmpty() &&
            expenses.isEmpty() &&
            transfers.isEmpty()
    }

    fun totalCount(): Int {
        return incomes.size + subscriptions.size + recurringExpenses.size + expenses.size + transfers.size
    }
}
