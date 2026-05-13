package com.example.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.example.moneymanager.AccountType
import com.example.moneymanager.PaymentAccount
import com.example.moneymanager.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

internal class ManageAccountsDialogController(
    private val context: Context,
    private val accountsProvider: () -> List<PaymentAccount>,
    private val ensureDefaultPaymentAccounts: () -> Unit,
    private val getPaymentAccountDisplayName: (PaymentAccount) -> String,
    private val getAccountTypeLabel: (AccountType) -> String,
    private val onEditAccount: (Long?) -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_manage_accounts, null)
        val layoutAccounts = dialogView.findViewById<LinearLayout>(R.id.layoutAccountRows)
        val btnAddAccount = dialogView.findViewById<Button>(R.id.btnAddAccount)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseAccounts)
        ensureDefaultPaymentAccounts()

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        accountsProvider()
            .sortedBy { getPaymentAccountDisplayName(it).lowercase(Locale.getDefault()) }
            .forEach { account ->
                val button = LayoutInflater.from(context)
                    .inflate(R.layout.item_category_manage, layoutAccounts, false)
                    .findViewById<Button>(R.id.btnCategoryRow)
                button.text = "${getPaymentAccountDisplayName(account)} \u00B7 ${getAccountTypeLabel(account.type)}"
                button.setOnClickListener {
                    dialog.dismiss()
                    onEditAccount(account.id)
                }
                button.setOnLongClickListener {
                    dialog.dismiss()
                    onEditAccount(account.id)
                    true
                }
                layoutAccounts.addView(button)
            }

        btnAddAccount.setOnClickListener {
            dialog.dismiss()
            onEditAccount(null)
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        styleBottomSheet(dialog)
    }
}
