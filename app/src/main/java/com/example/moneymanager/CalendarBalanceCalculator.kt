package com.example.moneymanager

import java.util.Calendar

internal class CalendarBalanceCalculator(
    private val incomesProvider: () -> List<Income>,
    private val subscriptionsProvider: () -> List<Subscription>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val expensesProvider: () -> List<Expense>,
    private val transfersProvider: () -> List<AccountTransfer>,
    private val getBaseBalanceForAccount: (String?) -> Double,
    private val matchesAccountFilter: (Long, String?) -> Boolean,
    private val matchesTransferAccountFilter: (AccountTransfer, String?) -> Boolean,
    private val getTransferDeltaForFilter: (AccountTransfer, String?) -> Double
) {
    private val dateFormatter = UiDateFormatter()

    fun getProjectedBalanceForDate(dateIso: String, accountFilter: String = FILTER_ALL): Double {
        val target = parseIsoDate(dateIso) ?: return 0.0
        return getBalanceUntilDate(target, accountFilter)
    }

    fun getBalanceUntilDate(target: Calendar, accountFilter: String = FILTER_ALL): Double {
        val baseBalance = getBaseBalanceForAccount(accountFilter)
        val incomeTotal = incomesProvider()
            .filter { income ->
                if (!matchesAccountFilter(income.accountId, accountFilter)) return@filter false
                if (income.period == "monthly") {
                    true
                } else {
                    val incomeDate = parseIsoDate(income.expectedDate) ?: return@filter false
                    !incomeDate.after(target)
                }
            }
            .sumOf { income ->
                if (income.period == "monthly") {
                    income.amount * countIncomeOccurrencesUntil(income, target)
                } else {
                    income.amount
                }
            }

        val expenseTotal = expensesProvider()
            .filter { expense ->
                if (!matchesAccountFilter(expense.accountId, accountFilter)) return@filter false
                val expenseDate = parseIsoDate(expense.date) ?: return@filter false
                !expenseDate.after(target)
            }
            .sumOf { it.amount }

        val subscriptionsTotal = subscriptionsProvider()
            .filter { matchesAccountFilter(it.accountId, accountFilter) }
            .sumOf { subscription ->
                getPaymentScheduleTotalUntil(
                    startIso = subscription.nextChargeDate,
                    period = subscription.period,
                    baseAmount = subscription.amount,
                    target = target,
                    skippedDates = emptyList(),
                    overrides = subscription.occurrenceOverrides,
                    isOccurrenceEnabled = { originalDate ->
                        isSubscriptionLifecycleActiveOnDate(
                            subscription.lifecycleStatus,
                            subscription.lifecycleDate,
                            originalDate
                        )
                    }
                )
            }

        val recurringExpenseTotal = recurringExpensesProvider()
            .filter { matchesAccountFilter(it.accountId, accountFilter) }
            .sumOf { recurringExpense ->
                getPaymentScheduleTotalUntil(
                    startIso = recurringExpense.startDate,
                    period = recurringExpense.period,
                    baseAmount = recurringExpense.amount,
                    target = target,
                    skippedDates = recurringExpense.skippedDates,
                    overrides = recurringExpense.occurrenceOverrides
                )
            }

        val transferDelta = transfersProvider()
            .filter { transfer ->
                val transferDate = parseIsoDate(transfer.date) ?: return@filter false
                !transferDate.after(target) && matchesTransferAccountFilter(transfer, accountFilter)
            }
            .sumOf { transfer -> getTransferDeltaForFilter(transfer, accountFilter) }

        return baseBalance + incomeTotal - expenseTotal - subscriptionsTotal - recurringExpenseTotal + transferDelta
    }

    private fun countIncomeOccurrencesUntil(income: Income, target: Calendar): Int {
        return CalendarRecurrence.countMonthlyOccurrencesUntil(
            startIso = income.expectedDate,
            targetIso = dateFormatter.isoDateFormatter().format(target.time)
        )
    }

    private fun getPaymentScheduleTotalUntil(
        startIso: String?,
        period: String,
        baseAmount: Double,
        target: Calendar,
        skippedDates: List<String>,
        overrides: List<PaymentOccurrenceOverride>,
        isOccurrenceEnabled: (String) -> Boolean = { true }
    ): Double {
        val occurrenceDate = parseIsoDate(startIso) ?: return 0.0
        val targetDate = dateFormatter.normalizedCalendar(target)
        val anchorDay = occurrenceDate.get(Calendar.DAY_OF_MONTH)
        val lastOriginalDate = overrides
            .filter { paymentOverride ->
                val displayDate = parseIsoDate(paymentOverride.date ?: paymentOverride.originalDate)
                displayDate != null && !displayDate.after(targetDate)
            }
            .mapNotNull { parseIsoDate(it.originalDate) }
            .maxByOrNull { it.timeInMillis }
        val iterationEnd = listOfNotNull(targetDate, lastOriginalDate).maxByOrNull { it.timeInMillis } ?: targetDate

        var total = 0.0
        while (!occurrenceDate.after(iterationEnd)) {
            val originalDate = dateFormatter.isoDateFormatter().format(occurrenceDate.time)
            val paymentOverride = findPaymentOverride(overrides, originalDate)
            val displayDate = parseIsoDate(paymentOverride?.date ?: originalDate)
            if (
                displayDate != null &&
                !displayDate.after(targetDate) &&
                isOccurrenceEnabled(originalDate) &&
                !isPaymentSkipped(skippedDates, originalDate)
            ) {
                total += paymentOverride?.amount ?: baseAmount
            }
            if (period == "yearly") {
                occurrenceDate.add(Calendar.YEAR, 1)
            } else {
                occurrenceDate.add(Calendar.MONTH, 1)
            }
            occurrenceDate.set(
                Calendar.DAY_OF_MONTH,
                minOf(anchorDay, occurrenceDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            )
        }
        return total
    }

    private fun parseIsoDate(value: String?): Calendar? {
        return dateFormatter.parseIsoDate(value)
    }
}
