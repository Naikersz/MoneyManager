package com.example.moneymanager

import java.util.Calendar

/**
 * Owns the three transaction lists and all derived calculations.
 * Pass an instance into MainActivity; call saveData() after every mutation.
 */
class TransactionManager {

    val incomes      = mutableListOf<Income>()
    val subscriptions = mutableListOf<Subscription>()
    val expenses     = mutableListOf<Expense>()

    // ── Totals ────────────────────────────────────────────────────────────────

    fun totalIncome(filter: (Income) -> Boolean = { true }): Double =
        incomes.filter(filter).sumOf { it.amount }

    fun totalSubscriptions(filter: (Subscription) -> Boolean = { true }): Double =
        subscriptions.filter(filter).sumOf { it.amount }

    fun totalExpenses(filter: (Expense) -> Boolean = { true }): Double =
        expenses.filter(filter).sumOf { it.amount }

    /** Remaining = income − expenses (subscriptions excluded by default). */
    fun remaining(
        incomeFilter: (Income) -> Boolean = { true },
        expenseFilter: (Expense) -> Boolean = { true }
    ): Double = totalIncome(incomeFilter) - totalExpenses(expenseFilter)

    // ── Month helpers ─────────────────────────────────────────────────────────

    private fun Calendar.matchesMonthOf(other: Calendar) =
        get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            get(Calendar.MONTH) == other.get(Calendar.MONTH)

    fun incomesForMonth(cal: Calendar): List<Income> = incomes.filter { income ->
        val date = parseIsoDate(income.expectedDate ?: income.date) ?: return@filter true
        date.matchesMonthOf(cal)
    }

    fun subscriptionsForMonth(cal: Calendar): List<Subscription> = subscriptions.filter { sub ->
        val date = parseIsoDate(sub.nextChargeDate) ?: return@filter true
        date.matchesMonthOf(cal)
    }

    fun expensesForMonth(cal: Calendar): List<Expense> = expenses.filter { expense ->
        val date = parseIsoDate(expense.date) ?: return@filter false
        date.matchesMonthOf(cal)
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addIncome(income: Income)                    { incomes.add(income) }
    fun updateIncome(index: Int, income: Income)     { incomes[index] = income }
    fun removeIncome(index: Int)                     { incomes.removeAt(index) }

    fun addSubscription(sub: Subscription)           { subscriptions.add(sub) }
    fun updateSubscription(index: Int, sub: Subscription) { subscriptions[index] = sub }
    fun removeSubscription(index: Int)               { subscriptions.removeAt(index) }

    fun addExpense(expense: Expense)                 { expenses.add(expense) }
    fun updateExpense(index: Int, expense: Expense)  { expenses[index] = expense }
    fun removeExpense(index: Int)                    { expenses.removeAt(index) }

    // ── Item list (for RecyclerView) ──────────────────────────────────────────

    fun buildItems(): List<Item> = buildList {
        incomes.forEachIndexed { i, inc ->
            add(Item(inc.name, inc.amount, inc.type, inc.expectedDate ?: inc.date, ItemType.INCOME, i))
        }
        subscriptions.forEachIndexed { i, sub ->
            add(Item(sub.name, sub.amount, sub.period, sub.nextChargeDate ?: "", ItemType.SUBSCRIPTION, i))
        }
        expenses.forEachIndexed { i, exp ->
            add(Item(exp.name, exp.amount, exp.category ?: "", exp.date, ItemType.EXPENSE, i))
        }
    }
}

// Date parser helper
private fun parseIsoDate(iso: String?): Calendar? {
    if (iso.isNullOrBlank()) return null
    return try {
        val parts = iso.split("-")
        if (parts.size != 3) return null
        Calendar.getInstance().apply {
            set(Calendar.YEAR, parts[0].toIntOrNull() ?: return@parseIsoDate null)
            set(Calendar.MONTH, (parts[1].toIntOrNull() ?: return@parseIsoDate null) - 1)
            set(Calendar.DAY_OF_MONTH, parts[2].toIntOrNull() ?: return@parseIsoDate null)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    } catch (e: Exception) {
        null
    }
}
