package com.example.moneymanager

import kotlin.math.abs

internal class PaymentOccurrenceOverrideController(
    private val subscriptionsProvider: () -> MutableList<Subscription>,
    private val recurringExpensesProvider: () -> MutableList<RecurringExpense>,
    private val onChanged: () -> Unit
) {
    fun getBaseAmount(event: CalendarEvent): Double? {
        return when (event.type) {
            ItemType.SUBSCRIPTION -> subscriptionsProvider().getOrNull(event.sourceIndex)?.amount
            ItemType.RECURRING_EXPENSE -> recurringExpensesProvider().getOrNull(event.sourceIndex)?.amount
            else -> null
        }
    }

    fun getOverride(event: CalendarEvent): PaymentOccurrenceOverride? {
        val originalDate = event.originalOccurrenceDate ?: event.occurrenceDate ?: return null
        return when (event.type) {
            ItemType.SUBSCRIPTION -> subscriptionsProvider().getOrNull(event.sourceIndex)
                ?.occurrenceOverrides
                ?.let { findPaymentOverride(it, originalDate) }
            ItemType.RECURRING_EXPENSE -> recurringExpensesProvider().getOrNull(event.sourceIndex)
                ?.occurrenceOverrides
                ?.let { findPaymentOverride(it, originalDate) }
            else -> null
        }
    }

    fun saveOverride(
        event: CalendarEvent,
        originalDate: String,
        selectedDate: String,
        amount: Double,
        baseAmount: Double
    ) {
        val amountOverride = amount.takeUnless { abs(it - baseAmount) < 0.005 }
        val dateOverride = selectedDate.takeUnless { it == originalDate }
        val paymentOverride = PaymentOccurrenceOverride(
            originalDate = originalDate,
            date = dateOverride,
            amount = amountOverride
        )

        if (paymentOverride.date == null && paymentOverride.amount == null) {
            removeOverride(event, originalDate)
            return
        }

        when (event.type) {
            ItemType.SUBSCRIPTION -> {
                val subscriptions = subscriptionsProvider()
                val index = event.sourceIndex
                val subscription = subscriptions.getOrNull(index) ?: return
                subscriptions[index] = subscription.copy(
                    occurrenceOverrides = replacePaymentOverride(subscription.occurrenceOverrides, paymentOverride)
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpenses = recurringExpensesProvider()
                val index = event.sourceIndex
                val recurringExpense = recurringExpenses.getOrNull(index) ?: return
                recurringExpenses[index] = recurringExpense.copy(
                    occurrenceOverrides = replacePaymentOverride(recurringExpense.occurrenceOverrides, paymentOverride)
                )
            }
            else -> Unit
        }
        onChanged()
    }

    fun removeOverride(event: CalendarEvent, originalDate: String) {
        when (event.type) {
            ItemType.SUBSCRIPTION -> {
                val subscriptions = subscriptionsProvider()
                val index = event.sourceIndex
                val subscription = subscriptions.getOrNull(index) ?: return
                subscriptions[index] = subscription.copy(
                    occurrenceOverrides = subscription.occurrenceOverrides.filterNot { it.originalDate == originalDate }
                )
            }
            ItemType.RECURRING_EXPENSE -> {
                val recurringExpenses = recurringExpensesProvider()
                val index = event.sourceIndex
                val recurringExpense = recurringExpenses.getOrNull(index) ?: return
                recurringExpenses[index] = recurringExpense.copy(
                    occurrenceOverrides = recurringExpense.occurrenceOverrides.filterNot { it.originalDate == originalDate }
                )
            }
            else -> Unit
        }
        onChanged()
    }
}
