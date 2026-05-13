package com.example.moneymanager

internal fun buildExactAccountFilter(accountId: Long): String = "account_$accountId"

internal fun parseAccountIdFromFilter(filter: String?): Long? {
    if (filter.isNullOrBlank()) return null
    return filter.removePrefix("account_").takeIf { filter.startsWith("account_") }?.toLongOrNull()
}

internal fun isCardType(type: AccountType): Boolean {
    return type == AccountType.BANK_CARD || type == AccountType.BANK_ACCOUNT
}

internal fun isWalletType(type: AccountType): Boolean {
    return type == AccountType.PAYPAL || type == AccountType.OTHER
}

internal fun normalizeLegacyAccountSource(account: String?): String {
    return when (account) {
        ACCOUNT_BANK -> ACCOUNT_BANK
        ACCOUNT_PAYPAL -> ACCOUNT_PAYPAL
        else -> ACCOUNT_CASH
    }
}

internal fun matchesAccountFilter(account: PaymentAccount?, filter: String?): Boolean {
    account ?: return false
    return when (filter) {
        null, FILTER_ALL -> true
        FILTER_GROUP_CARDS -> isCardType(account.type)
        FILTER_GROUP_WALLETS -> isWalletType(account.type)
        FILTER_GROUP_CASH -> account.type == AccountType.CASH
        else -> parseAccountIdFromFilter(filter) == account.id
    }
}
