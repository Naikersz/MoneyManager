package com.example.moneymanager

fun calendarEventSignedAmount(event: CalendarEvent): Double {
    return when (event.type) {
        ItemType.INCOME -> event.amount
        ItemType.TRANSFER -> event.accountDelta ?: 0.0
        else -> -event.amount
    }
}

fun calendarEventsDelta(events: Iterable<CalendarEvent>): Double {
    return events.sumOf(::calendarEventSignedAmount)
}

fun calendarEventPriority(type: ItemType): Int {
    return when (type) {
        ItemType.INCOME -> 0
        ItemType.SUBSCRIPTION -> 1
        ItemType.RECURRING_EXPENSE -> 2
        ItemType.EXPENSE -> 3
        ItemType.TRANSFER -> 4
    }
}

fun canUpdateCalendarPaymentStatus(event: CalendarEvent): Boolean {
    return event.occurrenceDate != null &&
        event.type == ItemType.RECURRING_EXPENSE &&
        !event.isPaid &&
        !event.isSkipped
}
