package com.example.moneymanager.ui

import android.content.Context
import com.example.moneymanager.AccountType
import com.example.moneymanager.DEFAULT_CARD_ACCOUNT_ID
import com.example.moneymanager.DEFAULT_CASH_ACCOUNT_ID
import com.example.moneymanager.FILTER_ALL
import com.example.moneymanager.FILTER_GROUP_CARDS
import com.example.moneymanager.FILTER_GROUP_CASH
import com.example.moneymanager.FILTER_GROUP_WALLETS
import com.example.moneymanager.ItemType
import com.example.moneymanager.PaymentAccount
import com.example.moneymanager.R
import com.example.moneymanager.parseAccountIdFromFilter

internal class AccountDisplayController(
    private val context: Context,
    private val accountProvider: (Long?) -> PaymentAccount?
) {
    fun getCalendarFilterLabel(filter: String): String {
        return when (filter) {
            FILTER_GROUP_CARDS -> context.getString(R.string.calendar_filter_all_cards)
            FILTER_GROUP_WALLETS -> context.getString(R.string.calendar_filter_all_wallets)
            FILTER_GROUP_CASH -> context.getString(R.string.calendar_filter_all_cash)
            FILTER_ALL -> context.getString(R.string.calendar_filter_all_sources)
            else -> accountProvider(parseAccountIdFromFilter(filter))?.let(::getPaymentAccountDisplayName)
                ?: context.getString(R.string.calendar_filter_all_sources)
        }
    }

    fun getPaymentAccountDisplayName(account: PaymentAccount): String {
        val name = account.name.trim()
        val cashNames = setOf("Cash", "Bargeld", "\u041d\u0430\u043b\u0438\u0447\u043d\u044b\u0435", "\u041d\u0430\u043b\u0438\u0447\u043a\u0430")
        val cardNames = setOf("Card", "Karte", "\u041a\u0430\u0440\u0442\u0430")
        return when {
            account.id == DEFAULT_CASH_ACCOUNT_ID && name in cashNames -> context.getString(R.string.account_cash)
            account.id == DEFAULT_CARD_ACCOUNT_ID && name in cardNames -> context.getString(R.string.account_default_card)
            else -> account.name
        }
    }

    fun getPaymentAccountName(accountId: Long?): String {
        return accountProvider(accountId)?.let(::getPaymentAccountDisplayName) ?: context.getString(R.string.account_cash)
    }

    fun getPaymentAccountType(accountId: Long?): AccountType {
        return accountProvider(accountId)?.type ?: AccountType.CASH
    }

    fun getPaymentAccountIcon(accountId: Long?): String {
        return when (getPaymentAccountType(accountId)) {
            AccountType.BANK_CARD,
            AccountType.BANK_ACCOUNT -> "\uD83D\uDCB3"
            AccountType.PAYPAL -> "\uD83D\uDC5B"
            AccountType.CASH -> "\uD83D\uDCB5"
            AccountType.OTHER -> "\uD83D\uDC5B"
        }
    }

    fun getItemMarker(type: ItemType): String {
        return when (type) {
            ItemType.INCOME -> "\uD83D\uDFE2"
            ItemType.SUBSCRIPTION -> "\uD83D\uDFE3"
            ItemType.RECURRING_EXPENSE -> "\uD83D\uDD01"
            ItemType.EXPENSE -> "\uD83D\uDD34"
            ItemType.TRANSFER -> "\u2194"
        }
    }

    fun getAccountTypeLabel(type: AccountType): String {
        return when (type) {
            AccountType.BANK_CARD -> context.getString(R.string.account_type_bank_card)
            AccountType.BANK_ACCOUNT -> context.getString(R.string.account_type_bank_account)
            AccountType.PAYPAL -> context.getString(R.string.account_type_paypal)
            AccountType.CASH -> context.getString(R.string.account_type_cash)
            AccountType.OTHER -> context.getString(R.string.account_type_other)
        }
    }

    fun getAccountManagementSummary(accounts: List<PaymentAccount>): String {
        return accounts.joinToString(" \u00B7 ") { getPaymentAccountDisplayName(it) }
    }
}
