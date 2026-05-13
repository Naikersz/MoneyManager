package com.example.moneymanager.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.moneymanager.AccountActivityLine
import com.example.moneymanager.AccountTransfer
import com.example.moneymanager.AccountType
import com.example.moneymanager.Expense
import com.example.moneymanager.Income
import com.example.moneymanager.PaymentAccount
import com.example.moneymanager.R
import com.example.moneymanager.RecurringExpense
import com.example.moneymanager.Subscription
import com.example.moneymanager.addPressAnimation
import com.example.moneymanager.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class AccountDetailsDialogController(
    private val context: Context,
    private val accountProvider: (Long) -> PaymentAccount?,
    private val incomesProvider: () -> List<Income>,
    private val expensesProvider: () -> List<Expense>,
    private val subscriptionsProvider: () -> List<Subscription>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val transfersProvider: () -> List<AccountTransfer>,
    private val getPaymentAccountDisplayName: (PaymentAccount) -> String,
    private val getAccountTypeLabel: (AccountType) -> String,
    private val getCurrentBalanceForAccount: (PaymentAccount) -> Double,
    private val formatAssetAmount: (Double) -> String,
    private val formatDisplayDate: (String?) -> String,
    private val getPaymentAccountName: (Long?) -> String,
    private val getAssetAmountColor: (Double) -> Int,
    private val onEditAccount: (Long) -> Unit,
    private val onAddTransfer: () -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun show(accountId: Long) {
        val account = accountProvider(accountId) ?: return
        val dialogView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = context.getDrawable(R.drawable.bg_home_balance_card)
            setPadding(context.dpToPx(16), context.dpToPx(16), context.dpToPx(16), context.dpToPx(16))
        }

        dialogView.addView(TextView(context).apply {
            text = getPaymentAccountDisplayName(account)
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 18f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        dialogView.addView(TextView(context).apply {
            text = getAccountTypeLabel(account.type)
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, context.dpToPx(4), 0, 0)
        })
        dialogView.addView(TextView(context).apply {
            text = formatAssetAmount(getCurrentBalanceForAccount(account))
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 26f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, context.dpToPx(10), 0, 0)
        })

        val dialog = BottomSheetDialog(context)
        val actions = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, context.dpToPx(12), 0, context.dpToPx(14))
        }
        actions.addView(createActionPill(context.getString(R.string.edit), primary = true) {
            dialog.dismiss()
            onEditAccount(account.id)
        })
        actions.addView(createActionPill(context.getString(R.string.add_transfer_menu), primary = false) {
            dialog.dismiss()
            onAddTransfer()
        })
        dialogView.addView(actions)

        dialogView.addView(TextView(context).apply {
            text = context.getString(R.string.account_details_recent)
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, context.dpToPx(4), 0, context.dpToPx(6))
        })

        val activity = getAccountActivityLines(account.id).take(5)
        if (activity.isEmpty()) {
            dialogView.addView(TextView(context).apply {
                text = context.getString(R.string.account_details_no_activity)
                setTextColor(context.getColor(R.color.text_secondary))
                textSize = 14f
                setPadding(0, context.dpToPx(4), 0, 0)
            })
        } else {
            activity.forEach { line ->
                dialogView.addView(createActivityRow(line))
            }
        }

        dialog.setContentView(dialogView)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createActionPill(
        textValue: String,
        primary: Boolean,
        onClick: () -> Unit
    ): TextView {
        return TextView(context).apply {
            text = textValue
            gravity = Gravity.CENTER
            minWidth = context.dpToPx(72)
            minHeight = context.dpToPx(48)
            setPadding(context.dpToPx(12), 0, context.dpToPx(12), 0)
            textSize = 12f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(context.getColor(if (primary) R.color.button_text_light else R.color.text_primary))
            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = context.dpToPx(16).toFloat()
                setColor(context.getColor(if (primary) R.color.button_dark else R.color.card_surface_alt))
                if (!primary) {
                    setStroke(context.dpToPx(1), context.getColor(R.color.home_line))
                }
            }
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                context.dpToPx(48)
            ).apply {
                marginEnd = context.dpToPx(8)
            }
        }.also { it.addPressAnimation() }
    }

    private fun createActivityRow(line: AccountActivityLine): View {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, context.dpToPx(8), 0, context.dpToPx(8))
        }
        val textGroup = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        textGroup.addView(TextView(context).apply {
            text = line.title
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 14f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        textGroup.addView(TextView(context).apply {
            text = line.meta
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 12f
            setPadding(0, context.dpToPx(2), 0, 0)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        row.addView(textGroup)
        row.addView(TextView(context).apply {
            text = formatAssetAmount(line.amount)
            setTextColor(context.getColor(getAssetAmountColor(line.amount)))
            textSize = 13f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(context.dpToPx(10), 0, 0, 0)
        })
        return row
    }

    private fun getAccountActivityLines(accountId: Long): List<AccountActivityLine> {
        val lines = mutableListOf<AccountActivityLine>()
        incomesProvider().filter { it.accountId == accountId }.forEach {
            lines.add(AccountActivityLine(it.name, formatDisplayDate(it.date), it.amount, it.date))
        }
        expensesProvider().filter { it.accountId == accountId }.forEach {
            lines.add(AccountActivityLine(it.name, formatDisplayDate(it.date), -it.amount, it.date))
        }
        subscriptionsProvider().filter { it.accountId == accountId && !it.nextChargeDate.isNullOrBlank() }.forEach {
            val date = it.nextChargeDate.orEmpty()
            lines.add(AccountActivityLine(it.name, formatDisplayDate(date), -it.amount, date))
        }
        recurringExpensesProvider().filter { it.accountId == accountId && !it.startDate.isNullOrBlank() }.forEach {
            val date = it.startDate.orEmpty()
            lines.add(AccountActivityLine(it.name, formatDisplayDate(date), -it.amount, date))
        }
        transfersProvider().filter { it.fromAccountId == accountId || it.toAccountId == accountId }.forEach {
            val amount = when (accountId) {
                it.fromAccountId -> -it.amount
                it.toAccountId -> it.amount
                else -> 0.0
            }
            lines.add(
                AccountActivityLine(
                    title = it.note ?: context.getString(R.string.transfer),
                    meta = context.getString(
                        R.string.transfer_route,
                        getPaymentAccountName(it.fromAccountId),
                        getPaymentAccountName(it.toAccountId)
                    ),
                    amount = amount,
                    date = it.date
                )
            )
        }
        return lines.sortedByDescending { it.date }
    }
}
