package com.example.moneymanager

/**
 * Owns the PaymentAccount list, balance tracking, and filter helpers.
 */
class AccountManager {

    companion object {
        const val FILTER_ALL           = "all"
        const val FILTER_GROUP_CARDS   = "group_cards"
        const val FILTER_GROUP_WALLETS = "group_wallets"
        const val FILTER_GROUP_CASH    = "group_cash"

        private const val ACCOUNT_PREFIX = "account_"
    }

    val accounts = mutableListOf<Account>()
    var nextId = Transaction.DEFAULT_CARD_ACCOUNT_ID + 1L

    // ── Lookups ───────────────────────────────────────────────────────────────

    fun getById(id: Long): Account? = accounts.firstOrNull { it.id == id }

    fun getName(id: Long): String = getById(id)?.name ?: ""

    fun getIcon(id: Long): String {
        return when (getById(id)?.type) {
            AccountType.BANK_CARD    -> "💳"
            AccountType.BANK_ACCOUNT -> "🏦"
            AccountType.PAYPAL       -> "🅿️"
            AccountType.CASH         -> "💵"
            AccountType.OTHER        -> "💰"
            null                     -> "💰"
        }
    }

    fun getDisplayName(account: Account): String = account.name.ifBlank { getTypeLabel(account.type) }

    fun getTypeLabel(type: AccountType): String = when (type) {
        AccountType.BANK_CARD    -> "Card"
        AccountType.BANK_ACCOUNT -> "Bank Account"
        AccountType.PAYPAL       -> "PayPal"
        AccountType.CASH         -> "Cash"
        AccountType.OTHER        -> "Other"
    }

    // ── Type helpers ──────────────────────────────────────────────────────────

    fun isCardType(type: AccountType)   = type == AccountType.BANK_CARD
    fun isWalletType(type: AccountType) = type == AccountType.PAYPAL || type == AccountType.BANK_ACCOUNT

    fun cashAccounts()          = accounts.filter { it.type == AccountType.CASH }
    fun cardAndWalletAccounts() = accounts.filter { isCardType(it.type) || isWalletType(it.type) }

    // ── Filter matching ───────────────────────────────────────────────────────

    fun matchesFilter(accountId: Long, filter: String?): Boolean {
        val account = getById(accountId) ?: return false
        return when (filter) {
            null, FILTER_ALL           -> true
            FILTER_GROUP_CARDS         -> isCardType(account.type)
            FILTER_GROUP_WALLETS       -> isWalletType(account.type)
            FILTER_GROUP_CASH          -> account.type == AccountType.CASH
            else                       -> parseIdFromFilter(filter) == account.id
        }
    }

    fun baseBalance(filter: String?): Double =
        accounts.filter { matchesFilter(it.id, filter ?: FILTER_ALL) }.sumOf { it.balance }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addAccount(account: Account)                 { accounts.add(account) }
    fun updateBalance(id: Long, newBalance: Double)  {
        val i = accounts.indexOfFirst { it.id == id }
        if (i >= 0) accounts[i] = accounts[i].copy(balance = newBalance)
    }
    fun remove(id: Long)                             { accounts.removeAll { it.id == id } }

    fun newId(): Long = nextId++

    // ── Default account seeding ───────────────────────────────────────────────

    fun ensureDefaults() {
        if (accounts.none { it.id == Transaction.DEFAULT_CASH_ACCOUNT_ID })
            accounts.add(0, Account(Transaction.DEFAULT_CASH_ACCOUNT_ID, "Cash", AccountType.CASH))
        if (accounts.none { it.id == Transaction.DEFAULT_CARD_ACCOUNT_ID })
            accounts.add(1, Account(Transaction.DEFAULT_CARD_ACCOUNT_ID, "Card", AccountType.BANK_CARD))
    }

    // ── Filter label / id helpers ─────────────────────────────────────────────

    fun filterLabel(filter: String): String = when (filter) {
        FILTER_ALL           -> "All"
        FILTER_GROUP_CARDS   -> "Cards"
        FILTER_GROUP_WALLETS -> "Wallets"
        FILTER_GROUP_CASH    -> "Cash"
        else                 -> getName(parseIdFromFilter(filter))
    }

    private fun parseIdFromFilter(filter: String): Long = filter.removePrefix(ACCOUNT_PREFIX).toLongOrNull() ?: 0L
}
