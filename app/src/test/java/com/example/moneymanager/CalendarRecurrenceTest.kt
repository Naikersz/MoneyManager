package com.example.moneymanager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRecurrenceTest {
    @Test
    fun monthlyOccurrenceUsesLastDayWhenAnchorDayIsMissing() {
        assertTrue(CalendarRecurrence.recurringOccurrenceMatches("2026-01-31", "monthly", "2026-02-28"))
        assertFalse(CalendarRecurrence.recurringOccurrenceMatches("2026-01-31", "monthly", "2026-02-27"))
    }

    @Test
    fun monthlyOccurrenceKeepsAnchorAfterShortMonth() {
        assertTrue(CalendarRecurrence.recurringOccurrenceMatches("2026-01-31", "monthly", "2026-03-31"))
        assertFalse(CalendarRecurrence.recurringOccurrenceMatches("2026-01-31", "monthly", "2026-03-30"))
    }

    @Test
    fun yearlyLeapDayFallsBackToFebruaryLastDay() {
        assertTrue(CalendarRecurrence.recurringOccurrenceMatches("2024-02-29", "yearly", "2025-02-28"))
        assertFalse(CalendarRecurrence.recurringOccurrenceMatches("2024-02-29", "yearly", "2025-03-01"))
    }

    @Test
    fun skippedRecurringOccurrenceIsNotCounted() {
        val count = CalendarRecurrence.countRecurringOccurrencesUntil(
            startIso = "2026-01-31",
            period = "monthly",
            targetIso = "2026-03-31",
            skippedDates = listOf("2026-02-28")
        )

        assertEquals(2, count)
    }

    @Test
    fun monthlyIncomeCountPreservesAnchorDay() {
        assertEquals(3, CalendarRecurrence.countMonthlyOccurrencesUntil("2026-01-31", "2026-03-31"))
    }
}
