package com.example.moneymanager

import kotlin.math.abs

internal fun sanitizePaymentId(candidate: Long?, seed: String): Long {
    return candidate?.takeIf { it > 0L } ?: (System.currentTimeMillis() + abs(seed.hashCode().toLong()))
}

internal fun replacePaymentOverride(
    overrides: List<PaymentOccurrenceOverride>,
    paymentOverride: PaymentOccurrenceOverride
): List<PaymentOccurrenceOverride> {
    return (overrides.filterNot { it.originalDate == paymentOverride.originalDate } + paymentOverride)
        .sortedBy { it.originalDate }
}

internal fun findPaymentOverride(
    overrides: List<PaymentOccurrenceOverride>,
    originalDate: String
): PaymentOccurrenceOverride? {
    return overrides.lastOrNull { it.originalDate == originalDate }
}
