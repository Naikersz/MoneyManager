package com.example.moneymanager

import android.content.Context
import java.util.Calendar
import java.util.Locale

internal class CalendarEventsCalculator(
    private val context: Context,
    private val incomesProvider: () -> List<Income>,
    private val subscriptionsProvider: () -> List<Subscription>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val expensesProvider: () -> List<Expense>,
    private val transfersProvider: () -> List<AccountTransfer>,
    private val matchesAccountFilter: (Long, String?) -> Boolean,
    private val matchesTransferAccountFilter: (AccountTransfer, String?) -> Boolean,
    private val getTransferDeltaForFilter: (AccountTransfer, String?) -> Double,
    private val getPaymentAccountName: (Long?) -> String,
    private val getIncomeTypeDisplayName: (String) -> String,
    private val buildCategoryLabel: (String?, String?) -> String
) {
    private val dateFormatter = UiDateFormatter()

    fun getEventsForMonth(month: Calendar, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        return buildEventsByDate(month, accountFilter).values.flatten()
    }

    fun getEventsForDate(dateIso: String, accountFilter: String = FILTER_ALL): List<CalendarEvent> {
        val date = parseIsoDate(dateIso) ?: return emptyList()
        return buildEventsByDate(date, accountFilter)[dateIso].orEmpty()
    }

    fun getDateStringsForMonth(month: Calendar): List<String> {
        val calendar = (month.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (1..daysInMonth).map { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
            dateFormatter.isoDateFormatter().format(calendar.time)
        }
    }

    fun buildEventsByDate(month: Calendar, accountFilter: String = FILTER_ALL): Map<String, List<CalendarEvent>> {
        val dateStrings = getDateStringsForMonth(month)
        val monthDates = dateStrings.toSet()
        val eventsByDate = dateStrings.associateWith { mutableListOf<CalendarEvent>() }.toMutableMap()

        fun addEvent(displayDate: String, event: CalendarEvent) {
            eventsByDate[displayDate]?.add(event)
        }

        incomesProvider().forEachIndexed { index, income ->
            if (!matchesAccountFilter(income.accountId, accountFilter)) return@forEachIndexed
            val fallbackDateIso = parseIsoDate(income.date)
                ?.let { dateFormatter.isoDateFormatter().format(it.time) }
                ?: income.date
            if (income.period == "monthly") {
                val sourceIncome = if (income.expectedDate.isNullOrBlank()) {
                    income.copy(expectedDate = fallbackDateIso)
                } else {
                    income
                }
                dateStrings
                    .filter { dateIso -> incomeOccursOnDate(sourceIncome, dateIso) }
                    .forEach { dateIso -> addEvent(dateIso, buildIncomeEvent(income, index, dateIso)) }
            } else {
                val dateIso = income.expectedDate?.takeIf { it.isNotBlank() } ?: fallbackDateIso
                if (dateIso in monthDates) {
                    addEvent(dateIso, buildIncomeEvent(income, index, dateIso))
                }
            }
        }

        expensesProvider().forEachIndexed { index, expense ->
            if (expense.date in monthDates && matchesAccountFilter(expense.accountId, accountFilter)) {
                addEvent(expense.date, buildExpenseEvent(expense, index, expense.date))
            }
        }

        subscriptionsProvider().forEachIndexed { index, subscription ->
            if (!matchesAccountFilter(subscription.accountId, accountFilter)) return@forEachIndexed
            val canonicalDates = dateStrings.filter { dateIso ->
                subscriptionOccursOnDate(subscription, dateIso) &&
                    isSubscriptionLifecycleActiveOnDate(
                        subscription.lifecycleStatus,
                        subscription.lifecycleDate,
                        dateIso
                    )
            }
            canonicalDates.forEach { originalDate ->
                addSubscriptionOccurrenceEvent(eventsByDate, subscription, index, originalDate)
            }
            subscription.occurrenceOverrides
                .filter { paymentOverride -> paymentOverride.date in monthDates && paymentOverride.originalDate !in canonicalDates }
                .filter { paymentOverride ->
                    subscriptionOccursOnDate(subscription, paymentOverride.originalDate) &&
                        isSubscriptionLifecycleActiveOnDate(
                            subscription.lifecycleStatus,
                            subscription.lifecycleDate,
                            paymentOverride.originalDate
                        )
                }
                .forEach { paymentOverride ->
                    addSubscriptionOccurrenceEvent(eventsByDate, subscription, index, paymentOverride.originalDate)
                }
        }

        recurringExpensesProvider().forEachIndexed { index, recurringExpense ->
            if (!matchesAccountFilter(recurringExpense.accountId, accountFilter)) return@forEachIndexed
            val canonicalDates = dateStrings.filter { dateIso -> recurringExpenseOccursOnDate(recurringExpense, dateIso) }
            canonicalDates.forEach { originalDate ->
                addRecurringExpenseOccurrenceEvent(eventsByDate, recurringExpense, index, originalDate)
            }
            recurringExpense.occurrenceOverrides
                .filter { paymentOverride -> paymentOverride.date in monthDates && paymentOverride.originalDate !in canonicalDates }
                .filter { paymentOverride -> recurringExpenseOccursOnDate(recurringExpense, paymentOverride.originalDate) }
                .forEach { paymentOverride ->
                    addRecurringExpenseOccurrenceEvent(eventsByDate, recurringExpense, index, paymentOverride.originalDate)
                }
        }

        transfersProvider().forEachIndexed { index, transfer ->
            if (transfer.date in monthDates && matchesTransferAccountFilter(transfer, accountFilter)) {
                addEvent(transfer.date, buildTransferEvent(transfer, index, transfer.date, accountFilter))
            }
        }

        return eventsByDate.mapValues { (_, events) -> sortEvents(events) }
    }

    fun monthContainsIncomeOccurrence(income: Income, month: Calendar): Boolean {
        return (income.expectedDate.isNullOrBlank() && income.date.isBlank()) ||
            getIncomeOccurrenceDateInMonth(income, month) != null
    }

    fun getIncomeOccurrenceDateInMonth(income: Income, month: Calendar): String? {
        val source = if (income.expectedDate.isNullOrBlank()) {
            income.copy(expectedDate = income.date)
        } else {
            income
        }
        if (source.expectedDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            incomeOccursOnDate(source, dateIso)
        }
    }

    fun monthContainsSubscriptionOccurrence(subscription: Subscription, month: Calendar): Boolean {
        if (normalizeSubscriptionLifecycleStatus(subscription.lifecycleStatus) != SUBSCRIPTION_STATUS_ACTIVE) return true
        return subscription.nextChargeDate.isNullOrBlank() ||
            getSubscriptionOccurrenceDateInMonth(subscription, month) != null
    }

    fun getSubscriptionOccurrenceDateInMonth(subscription: Subscription, month: Calendar): String? {
        if (subscription.nextChargeDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            subscriptionOccursOnDate(subscription, dateIso) &&
                isSubscriptionLifecycleActiveOnDate(
                    subscription.lifecycleStatus,
                    subscription.lifecycleDate,
                    dateIso
                )
        }
    }

    fun monthContainsRecurringExpenseOccurrence(recurringExpense: RecurringExpense, month: Calendar): Boolean {
        return recurringExpense.startDate.isNullOrBlank() ||
            getRecurringExpenseOccurrenceDateInMonth(recurringExpense, month) != null
    }

    fun getRecurringExpenseOccurrenceDateInMonth(recurringExpense: RecurringExpense, month: Calendar): String? {
        if (recurringExpense.startDate.isNullOrBlank()) return null
        return getDateStringsForMonth(month).firstOrNull { dateIso ->
            recurringExpenseOccursOnDate(recurringExpense, dateIso)
        }
    }

    private fun addSubscriptionOccurrenceEvent(
        eventsByDate: MutableMap<String, MutableList<CalendarEvent>>,
        subscription: Subscription,
        sourceIndex: Int,
        originalDate: String
    ) {
        val paymentOverride = findPaymentOverride(subscription.occurrenceOverrides, originalDate)
        val displayDate = paymentOverride?.date ?: originalDate
        val targetEvents = eventsByDate[displayDate] ?: return

        targetEvents.add(
            CalendarEvent(
                type = ItemType.SUBSCRIPTION,
                name = subscription.name,
                amount = paymentOverride?.amount ?: subscription.amount,
                detail = buildEventDetail(
                    if (subscription.period == "yearly") {
                        context.getString(R.string.yearly)
                    } else {
                        context.getString(R.string.monthly)
                    },
                    subscription.accountId
                ),
                sourceIndex = sourceIndex,
                occurrenceDate = displayDate,
                originalOccurrenceDate = originalDate,
                isForecast = isFutureDate(displayDate)
            )
        )
    }

    private fun addRecurringExpenseOccurrenceEvent(
        eventsByDate: MutableMap<String, MutableList<CalendarEvent>>,
        recurringExpense: RecurringExpense,
        sourceIndex: Int,
        originalDate: String
    ) {
        val paymentOverride = findPaymentOverride(recurringExpense.occurrenceOverrides, originalDate)
        val displayDate = paymentOverride?.date ?: originalDate
        val targetEvents = eventsByDate[displayDate] ?: return
        val isSkipped = isPaymentSkipped(recurringExpense.skippedDates, originalDate)
        val isPaid = isPaymentPaid(recurringExpense.paidDates, originalDate)
        val isOverdue = isPastDate(displayDate) && !isPaid && !isSkipped

        targetEvents.add(
            CalendarEvent(
                type = ItemType.RECURRING_EXPENSE,
                name = recurringExpense.name,
                amount = if (isSkipped) 0.0 else paymentOverride?.amount ?: recurringExpense.amount,
                detail = appendPaymentStatus(
                    buildEventDetail(
                        appendTags(buildCategoryLabel(recurringExpense.category, recurringExpense.subcategory), recurringExpense.tags),
                        recurringExpense.accountId
                    ),
                    isPaid,
                    isSkipped
                ),
                sourceIndex = sourceIndex,
                occurrenceDate = displayDate,
                originalOccurrenceDate = originalDate,
                isPaid = isPaid,
                isSkipped = isSkipped,
                isForecast = isFutureDate(displayDate) && !isPaid && !isSkipped,
                isOverdue = isOverdue
            )
        )
    }

    private fun buildIncomeEvent(income: Income, sourceIndex: Int, dateIso: String): CalendarEvent {
        return CalendarEvent(
            type = ItemType.INCOME,
            name = income.name,
            amount = income.amount,
            detail = buildEventDetail(
                getIncomeTypeDisplayName(income.type.ifBlank { context.getString(R.string.income_default_name) }),
                income.accountId
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun buildExpenseEvent(expense: Expense, sourceIndex: Int, dateIso: String): CalendarEvent {
        return CalendarEvent(
            type = ItemType.EXPENSE,
            name = expense.name,
            amount = expense.amount,
            detail = buildEventDetail(
                appendTags(buildCategoryLabel(expense.category, expense.subcategory), expense.tags),
                expense.accountId
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun buildTransferEvent(
        transfer: AccountTransfer,
        sourceIndex: Int,
        dateIso: String,
        accountFilter: String
    ): CalendarEvent {
        return CalendarEvent(
            type = ItemType.TRANSFER,
            name = transfer.note ?: context.getString(R.string.transfer),
            amount = transfer.amount,
            detail = context.getString(
                R.string.transfer_route,
                getPaymentAccountName(transfer.fromAccountId),
                getPaymentAccountName(transfer.toAccountId)
            ),
            sourceIndex = sourceIndex,
            occurrenceDate = dateIso,
            accountDelta = getTransferDeltaForFilter(transfer, accountFilter),
            isForecast = isFutureDate(dateIso)
        )
    }

    private fun sortEvents(events: List<CalendarEvent>): List<CalendarEvent> {
        return events.sortedWith(
            compareBy<CalendarEvent> { calendarEventPriority(it.type) }
                .thenBy { it.name.lowercase(Locale.getDefault()) }
        )
    }

    private fun incomeOccursOnDate(income: Income, dateIso: String): Boolean {
        return CalendarRecurrence.monthlyOccurrenceMatches(income.expectedDate, dateIso)
    }

    private fun subscriptionOccursOnDate(subscription: Subscription, dateIso: String): Boolean {
        return CalendarRecurrence.recurringOccurrenceMatches(subscription.nextChargeDate, subscription.period, dateIso)
    }

    private fun recurringExpenseOccursOnDate(recurringExpense: RecurringExpense, dateIso: String): Boolean {
        return CalendarRecurrence.recurringOccurrenceMatches(recurringExpense.startDate, recurringExpense.period, dateIso)
    }

    private fun buildEventDetail(primary: String, accountId: Long): String {
        return "$primary \u00B7 ${getPaymentAccountName(accountId)}"
    }

    private fun appendPaymentStatus(detail: String, isPaid: Boolean, isSkipped: Boolean): String {
        val status = when {
            isSkipped -> context.getString(R.string.payment_status_skipped)
            isPaid -> context.getString(R.string.payment_status_paid)
            else -> return detail
        }
        return "$detail \u00B7 $status"
    }

    private fun parseIsoDate(value: String?): Calendar? {
        return dateFormatter.parseIsoDate(value)
    }

    private fun isFutureDate(dateIso: String): Boolean {
        val date = parseIsoDate(dateIso) ?: return false
        return date.after(dateFormatter.todayCalendar())
    }

    private fun isPastDate(dateIso: String): Boolean {
        val date = parseIsoDate(dateIso) ?: return false
        return date.before(dateFormatter.todayCalendar())
    }
}
