package com.example.moneymanager

// ─── Transaction models ───────────────────────────────────────────────────────

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val amount: Double,
    val date: String,
    val accountId: Long = DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null
) {
    companion object {
        const val DEFAULT_CASH_ACCOUNT_ID = 1L
        const val DEFAULT_CARD_ACCOUNT_ID = 2L
    }
}

data class Income(
    val name: String,
    val amount: Double,
    val date: String,
    val type: String,
    val expectedDate: String? = null,
    val period: String = "once",
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null
)

data class Subscription(
    val name: String,
    val amount: Double,
    val period: String,
    val nextChargeDate: String? = null,
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null
)

data class Expense(
    val name: String,
    val amount: Double,
    val date: String,
    val category: String? = null,
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null
)

// ─── Account models ───────────────────────────────────────────────────────────

data class Account(
    val id: Long,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0
)

// Alias kept for backward compatibility during migration
typealias PaymentAccount = Account

// ─── UI / display models ──────────────────────────────────────────────────────

data class Item(
    val name: String,
    val amount: Double,
    val meta: String,
    val trailing: String,
    val type: ItemType,
    val sourceIndex: Int
)

data class CalendarEvent(
    val type: ItemType,
    val name: String,
    val amount: Double,
    val detail: String
)

data class CategoryBudget(
    val category: String,
    val amount: Double,
    val period: String = "monthly"
)

// ─── Enums ────────────────────────────────────────────────────────────────────

enum class ItemType { INCOME, SUBSCRIPTION, EXPENSE }

enum class AccountType { BANK_CARD, BANK_ACCOUNT, PAYPAL, CASH, OTHER }

enum class QuickAction { INCOME, EXPENSE, SUBSCRIPTION, CATEGORY, CASH }
