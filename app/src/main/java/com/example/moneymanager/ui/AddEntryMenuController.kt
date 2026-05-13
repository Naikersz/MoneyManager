package com.example.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.moneymanager.R
import com.example.moneymanager.addPressAnimation
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class AddEntryMenuController(
    private val context: Context,
    private val formatDisplayDate: (String?) -> String,
    private val showChoiceBottomSheet: (String, List<String>, (String) -> Unit) -> Unit,
    private val onAddIncome: (String?) -> Unit,
    private val onAddExpense: (String?) -> Unit,
    private val onAddSubscription: () -> Unit,
    private val onAddRecurringExpense: () -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun showAddEntryMenu(initialDate: String? = null) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_entry_menu, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAddEntryMenuTitle)
        val btnIncome = dialogView.findViewById<Button>(R.id.btnAddMenuIncome)
        val btnExpense = dialogView.findViewById<Button>(R.id.btnAddMenuExpense)
        val btnSubscription = dialogView.findViewById<Button>(R.id.btnAddMenuSubscription)
        val btnRecurringExpense = dialogView.findViewById<Button>(R.id.btnAddMenuRecurringExpense)

        tvTitle.text = context.getString(R.string.add_record_title)
        btnIncome.text = context.getString(R.string.add_income_menu)
        btnExpense.text = context.getString(R.string.add_expense_menu)
        btnSubscription.text = context.getString(R.string.add_subscription_menu)
        btnRecurringExpense.text = context.getString(R.string.add_recurring_expense_menu)
        btnIncome.addPressAnimation()
        btnExpense.addPressAnimation()
        btnSubscription.addPressAnimation()
        btnRecurringExpense.addPressAnimation()

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnIncome.setOnClickListener {
            dialog.dismiss()
            onAddIncome(initialDate)
        }
        btnExpense.setOnClickListener {
            dialog.dismiss()
            onAddExpense(initialDate)
        }
        btnSubscription.setOnClickListener {
            dialog.dismiss()
            onAddSubscription()
        }
        btnRecurringExpense.setOnClickListener {
            dialog.dismiss()
            onAddRecurringExpense()
        }
        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showCalendarQuickAdd(dateIso: String) {
        val expenseLabel = context.getString(R.string.add_expense)
        val incomeLabel = context.getString(R.string.add_income)
        showChoiceBottomSheet(formatDisplayDate(dateIso), listOf(expenseLabel, incomeLabel)) { selected ->
            when (selected) {
                expenseLabel -> onAddExpense(dateIso)
                incomeLabel -> onAddIncome(dateIso)
            }
        }
    }
}
