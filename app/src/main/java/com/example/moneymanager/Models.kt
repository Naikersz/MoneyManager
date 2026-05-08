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

const val SUBSCRIPTION_STATUS_ACTIVE = "active"
const val SUBSCRIPTION_STATUS_PAUSED = "paused"
const val SUBSCRIPTION_STATUS_CANCELED = "canceled"

fun normalizeSubscriptionLifecycleStatus(status: String?): String {
    return when (status) {
        SUBSCRIPTION_STATUS_PAUSED -> SUBSCRIPTION_STATUS_PAUSED
        SUBSCRIPTION_STATUS_CANCELED -> SUBSCRIPTION_STATUS_CANCELED
        else -> SUBSCRIPTION_STATUS_ACTIVE
    }
}

fun isSubscriptionLifecycleActiveOnDate(status: String?, lifecycleDate: String?, dateIso: String): Boolean {
    return when (normalizeSubscriptionLifecycleStatus(status)) {
        SUBSCRIPTION_STATUS_PAUSED,
        SUBSCRIPTION_STATUS_CANCELED -> {
            val stopDate = lifecycleDate?.takeIf { it.isNotBlank() } ?: return false
            dateIso < stopDate
        }
        else -> true
    }
}

data class Subscription(
    val name: String,
    val amount: Double,
    val period: String,
    val nextChargeDate: String? = null,
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null,
    val id: Long = System.currentTimeMillis(),
    val remindersEnabled: Boolean = true,
    val reminderDaysBefore: Int = 1,
    val lifecycleStatus: String = SUBSCRIPTION_STATUS_ACTIVE,
    val lifecycleDate: String? = null,
    val paidDates: List<String> = emptyList(),
    val skippedDates: List<String> = emptyList(),
    val occurrenceOverrides: List<PaymentOccurrenceOverride> = emptyList()
)

data class Expense(
    val name: String,
    val amount: Double,
    val date: String,
    val category: String? = null,
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null,
    val subcategory: String? = null,
    val tags: List<String> = emptyList()
)

data class RecurringExpense(
    val name: String,
    val amount: Double,
    val category: String? = null,
    val period: String = "monthly",
    val startDate: String? = null,
    val accountId: Long = Transaction.DEFAULT_CASH_ACCOUNT_ID,
    val accountSource: String? = null,
    val favorite: Boolean = false,
    val id: Long = System.currentTimeMillis(),
    val remindersEnabled: Boolean = true,
    val reminderDaysBefore: Int = 1,
    val paidDates: List<String> = emptyList(),
    val skippedDates: List<String> = emptyList(),
    val subcategory: String? = null,
    val tags: List<String> = emptyList(),
    val occurrenceOverrides: List<PaymentOccurrenceOverride> = emptyList()
)

data class PaymentOccurrenceOverride(
    val originalDate: String,
    val date: String? = null,
    val amount: Double? = null
)

// ─── Account models ───────────────────────────────────────────────────────────

data class AccountTransfer(
    val fromAccountId: Long,
    val toAccountId: Long,
    val amount: Double,
    val date: String,
    val note: String? = null,
    val id: Long = System.currentTimeMillis()
)

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
    val detail: String,
    val sourceIndex: Int = -1,
    val occurrenceDate: String? = null,
    val isPaid: Boolean = false,
    val isSkipped: Boolean = false,
    val accountDelta: Double? = null,
    val isForecast: Boolean = false,
    val isOverdue: Boolean = false,
    val originalOccurrenceDate: String? = null
)

data class CategoryBudget(
    val category: String,
    val amount: Double,
    val period: String = "monthly"
)

data class MoneyManagerBackup(
    val version: Int = 1,
    val exportedAt: String = "",
    val incomes: List<Income>? = emptyList(),
    val subscriptions: List<Subscription>? = emptyList(),
    val recurringExpenses: List<RecurringExpense>? = emptyList(),
    val expenses: List<Expense>? = emptyList(),
    val transfers: List<AccountTransfer>? = emptyList(),
    val categories: List<String>? = emptyList(),
    val favoriteCategories: List<String>? = emptyList(),
    val savedTags: List<String>? = emptyList(),
    val savedSubcategories: List<String>? = emptyList(),
    val categoryBudgets: List<CategoryBudget>? = emptyList(),
    val monthlyGoalAmount: Double = 0.0,
    val monthlyGoalIncludeExpenses: Boolean = true,
    val monthlyGoalIncludeSubscriptions: Boolean = true,
    val monthlyGoalCategories: List<String>? = emptyList(),
    val paymentAccounts: List<PaymentAccount>? = emptyList(),
    val nextPaymentAccountId: Long = Transaction.DEFAULT_CARD_ACCOUNT_ID + 1,
    val cashAmount: Double = 0.0,
    val language: String? = "en",
    val theme: String? = "minimal",
    val currency: String? = "EUR"
)

// ─── Enums ────────────────────────────────────────────────────────────────────

enum class ItemType { INCOME, SUBSCRIPTION, RECURRING_EXPENSE, EXPENSE, TRANSFER }

enum class AccountType { BANK_CARD, BANK_ACCOUNT, PAYPAL, CASH, OTHER }

enum class QuickAction { INCOME, EXPENSE, RECURRING_EXPENSE, SUBSCRIPTION, TRANSFER, CATEGORY, CASH }
