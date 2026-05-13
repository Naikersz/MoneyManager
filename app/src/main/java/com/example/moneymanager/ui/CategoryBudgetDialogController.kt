package com.example.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.moneymanager.CategoryBudget
import com.example.moneymanager.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

internal class CategoryBudgetDialogController(
    private val context: Context,
    private val budgetsProvider: () -> MutableList<CategoryBudget>,
    private val displayedMonthProvider: () -> Calendar,
    private val getCategoryBudget: (String) -> CategoryBudget?,
    private val getCategorySpentForMonth: (String, Calendar) -> Double,
    private val formatAmount: (Double) -> String,
    private val onChanged: () -> Unit,
    private val focusAmountInput: (EditText) -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun show(category: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_category_budget, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCategoryBudgetTitle)
        val tvSpent = dialogView.findViewById<TextView>(R.id.tvCategoryBudgetSpent)
        val etAmount = dialogView.findViewById<EditText>(R.id.etCategoryBudgetAmount)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCategoryBudgetDialog)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCategoryBudgetDialog)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveCategoryBudgetDialog)
        val existingBudget = getCategoryBudget(category)

        tvTitle.text = context.getString(R.string.category_budget_title, category)
        tvSpent.text = context.getString(
            R.string.category_budget_spent,
            formatAmount(getCategorySpentForMonth(category, displayedMonthProvider()))
        )
        if (existingBudget != null) {
            etAmount.setText(existingBudget.amount.toString())
            etAmount.selectAll()
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            budgetsProvider().removeAll { it.category == category }
            onChanged()
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().trim().replace(',', '.').toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                return@setOnClickListener
            }
            budgetsProvider().removeAll { it.category == category }
            budgetsProvider().add(CategoryBudget(category = category, amount = amount))
            onChanged()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }
}
