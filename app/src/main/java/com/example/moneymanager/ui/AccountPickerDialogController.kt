package com.example.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.moneymanager.AccountType
import com.example.moneymanager.FILTER_ALL
import com.example.moneymanager.FILTER_GROUP_CARDS
import com.example.moneymanager.FILTER_GROUP_CASH
import com.example.moneymanager.FILTER_GROUP_WALLETS
import com.example.moneymanager.PaymentAccount
import com.example.moneymanager.R
import com.example.moneymanager.buildExactAccountFilter
import com.example.moneymanager.dpToPx
import com.example.moneymanager.isCardType
import com.example.moneymanager.isWalletType
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

internal class AccountPickerDialogController(
    private val context: Context,
    private val accountsProvider: () -> List<PaymentAccount>,
    private val ensureDefaultPaymentAccounts: () -> Unit,
    private val getPaymentAccountDisplayName: (PaymentAccount) -> String,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun show(onSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_account_picker, null)
        val layoutOptions = dialogView.findViewById<LinearLayout>(R.id.layoutAccountPickerOptions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccountPicker)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        fun addOption(label: String, filter: String) {
            val button = LayoutInflater.from(context)
                .inflate(R.layout.item_category_manage, layoutOptions, false)
                .findViewById<Button>(R.id.btnCategoryRow)
            button.text = label
            button.setOnClickListener {
                onSelected(filter)
                dialog.dismiss()
            }
            layoutOptions.addView(button)
        }

        addOption(context.getString(R.string.calendar_filter_all_sources), FILTER_ALL)

        val paymentAccounts = accountsProvider()
        val hasCards = paymentAccounts.any { isCardType(it.type) }
        val hasWallets = paymentAccounts.any { isWalletType(it.type) }
        val hasCash = paymentAccounts.any { it.type == AccountType.CASH }
        if (hasCards || hasWallets || hasCash) {
            addSection(layoutOptions, context.getString(R.string.account_type))
            if (hasCards) addOption(context.getString(R.string.calendar_filter_all_cards), FILTER_GROUP_CARDS)
            if (hasWallets) addOption(context.getString(R.string.calendar_filter_all_wallets), FILTER_GROUP_WALLETS)
            if (hasCash) addOption(context.getString(R.string.calendar_filter_all_cash), FILTER_GROUP_CASH)
        }

        addSection(layoutOptions, context.getString(R.string.settings_accounts))
        paymentAccounts
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
                addOption(getPaymentAccountDisplayName(account), buildExactAccountFilter(account.id))
            }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun addSection(container: LinearLayout, title: String) {
        val label = TextView(context).apply {
            text = title
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(context.dpToPx(8), context.dpToPx(8), context.dpToPx(8), context.dpToPx(6))
        }
        container.addView(label)
    }
}
