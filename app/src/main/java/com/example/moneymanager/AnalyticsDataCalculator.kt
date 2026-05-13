package com.example.moneymanager

import android.content.Context
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class AnalyticsDataCalculator(
    private val context: Context,
    private val expensesProvider: () -> List<Expense>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val getEventsForMonth: (Calendar, String) -> List<CalendarEvent>,
    private val buildCategoryLabel: (String?, String?) -> String,
    private val formatCompactAmount: (Double) -> String,
    private val formatCompactSignedTotal: (Double) -> String
) {
    fun getMonthlyTrendPoints(
        endMonth: Calendar,
        accountFilter: String
    ): List<MonthlyTrendChartView.MonthPoint> {
        val end = (endMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return (5 downTo 0).map { offset ->
            val month = (end.clone() as Calendar).apply {
                add(Calendar.MONTH, -offset)
            }
            val events = getEventsForMonth(month, accountFilter)
            val incomeTotal = events
                .filter { it.type == ItemType.INCOME }
                .sumOf { it.amount }
            val outgoingTotal = events
                .filter {
                    it.type == ItemType.EXPENSE ||
                        it.type == ItemType.SUBSCRIPTION ||
                        it.type == ItemType.RECURRING_EXPENSE
                }
                .sumOf { it.amount }
            MonthlyTrendChartView.MonthPoint(
                label = SimpleDateFormat("MMM", Locale.getDefault()).format(month.time).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                },
                income = incomeTotal,
                outgoing = outgoingTotal,
                net = incomeTotal - outgoingTotal,
                incomeText = formatCompactAmount(incomeTotal),
                outgoingText = formatCompactAmount(outgoingTotal),
                netText = formatCompactSignedTotal(incomeTotal - outgoingTotal)
            )
        }
    }

    fun getMonthlyTrendDetails(
        endMonth: Calendar,
        accountFilter: String
    ): List<MonthlyTrendDetail> {
        val end = (endMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return (5 downTo 0).map { offset ->
            val month = (end.clone() as Calendar).apply {
                add(Calendar.MONTH, -offset)
            }
            val events = getEventsForMonth(month, accountFilter)
            val income = events
                .filter { it.type == ItemType.INCOME }
                .sumOf { it.amount }
            val expensesTotal = events
                .filter { it.type == ItemType.EXPENSE }
                .sumOf { it.amount }
            val subscriptionsTotal = events
                .filter { it.type == ItemType.SUBSCRIPTION }
                .sumOf { it.amount }
            val recurringTotal = events
                .filter { it.type == ItemType.RECURRING_EXPENSE }
                .sumOf { it.amount }
            val outgoing = expensesTotal + subscriptionsTotal + recurringTotal
            val net = income - outgoing
            MonthlyTrendDetail(
                label = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(month.time).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                },
                income = income,
                expenses = expensesTotal,
                subscriptions = subscriptionsTotal,
                recurring = recurringTotal,
                outgoing = outgoing,
                net = net
            )
        }
    }

    fun getCategoryTotals(month: Calendar, accountFilter: String): List<CategoryExpenseTotal> {
        val previousMonth = (month.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        val previousTotals = getCategoryAmountMap(previousMonth, accountFilter)
        return getCategoryAmountMap(month, accountFilter)
            .toList()
            .filter { it.second > 0.0 }
            .sortedByDescending { it.second }
            .mapIndexed { index, (category, amount) ->
                CategoryExpenseTotal(
                    category = category,
                    amount = amount,
                    previousAmount = previousTotals[category] ?: 0.0,
                    color = getSliceColor(index)
                )
            }
    }

    private fun getCategoryAmountMap(month: Calendar, accountFilter: String): Map<String, Double> {
        val totals = mutableMapOf<String, Double>()
        getEventsForMonth(month, accountFilter)
            .filter { event ->
                event.type == ItemType.EXPENSE ||
                    event.type == ItemType.RECURRING_EXPENSE ||
                    event.type == ItemType.SUBSCRIPTION
            }
            .filter { it.amount > 0.0 }
            .forEach { event ->
                val category = getCategoryLabel(event) ?: return@forEach
                totals[category] = (totals[category] ?: 0.0) + event.amount
            }
        return totals
    }

    private fun getCategoryLabel(event: CalendarEvent): String? {
        return when (event.type) {
            ItemType.EXPENSE -> expensesProvider().getOrNull(event.sourceIndex)
                ?.let { buildCategoryLabel(it.category, it.subcategory) }
            ItemType.RECURRING_EXPENSE -> recurringExpensesProvider().getOrNull(event.sourceIndex)
                ?.let { buildCategoryLabel(it.category, it.subcategory) }
            ItemType.SUBSCRIPTION -> context.getString(R.string.subscriptions)
            else -> null
        }
    }

    private fun getSliceColor(index: Int): Int {
        val colors = intArrayOf(
            context.getColor(R.color.calendar_accent),
            context.getColor(R.color.amount_negative),
            context.getColor(R.color.budget_warning),
            context.getColor(R.color.avatar_subscription_end),
            context.getColor(R.color.avatar_subscription_start),
            context.getColor(R.color.home_blue),
            Color.rgb(255, 174, 126),
            Color.rgb(201, 184, 255),
            Color.rgb(186, 230, 253),
            context.getColor(R.color.text_secondary)
        )
        return colors[index % colors.size]
    }
}
