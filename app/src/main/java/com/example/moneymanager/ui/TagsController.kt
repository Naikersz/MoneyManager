package com.example.moneymanager.ui

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.moneymanager.Expense
import com.example.moneymanager.R
import com.example.moneymanager.RecurringExpense
import com.example.moneymanager.dpToPx
import com.example.moneymanager.normalizeOptionalText
import com.example.moneymanager.parseTags
import com.example.moneymanager.tagsToInput
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

internal class TagsController(
    private val context: Context,
    private val savedTagsProvider: () -> MutableSet<String>,
    private val savedSubcategoriesProvider: () -> MutableSet<String>,
    private val expensesProvider: () -> List<Expense>,
    private val recurringExpensesProvider: () -> List<RecurringExpense>,
    private val showChoiceBottomSheet: (String, List<String>, (String) -> Unit) -> Unit,
    private val saveData: () -> Unit,
    private val updateSettingsPageValues: () -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun rememberTagsAndSubcategoriesFrom(
        importedExpenses: List<Expense>,
        importedRecurringExpenses: List<RecurringExpense>
    ) {
        val savedTags = savedTagsProvider()
        val savedSubcategories = savedSubcategoriesProvider()
        importedExpenses.forEach { expense ->
            savedTags.addAll(expense.tags)
            normalizeOptionalText(expense.subcategory)?.let(savedSubcategories::add)
        }
        importedRecurringExpenses.forEach { recurringExpense ->
            savedTags.addAll(recurringExpense.tags)
            normalizeOptionalText(recurringExpense.subcategory)?.let(savedSubcategories::add)
        }
    }

    fun getKnownTags(): List<String> {
        return (savedTagsProvider() + expensesProvider().flatMap { it.tags } + recurringExpensesProvider().flatMap { it.tags } + getPopularTags())
            .mapNotNull(::normalizeOptionalText)
            .distinctBy { it.lowercase(Locale.getDefault()) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
    }

    fun getKnownSubcategories(): List<String> {
        return (
            savedSubcategoriesProvider() +
                expensesProvider().mapNotNull { normalizeOptionalText(it.subcategory) } +
                recurringExpensesProvider().mapNotNull { normalizeOptionalText(it.subcategory) } +
                getPopularSubcategories()
            )
            .distinctBy { it.lowercase(Locale.getDefault()) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
    }

    fun showSubcategoryPicker(target: EditText) {
        val options = getKnownSubcategories()
        if (options.isEmpty()) return
        showChoiceBottomSheet(context.getString(R.string.choose_subcategory), options) { selected ->
            target.setText(selected)
            savedSubcategoriesProvider().add(selected)
        }
    }

    fun showTagPicker(target: EditText) {
        val options = getKnownTags()
        if (options.isEmpty()) return
        val selected = parseTags(target.text.toString()).toMutableSet()
        val checked = options.map { option ->
            selected.any { it.equals(option, ignoreCase = true) }
        }.toBooleanArray()
        showTagBottomSheet(options, checked, selected, target)
    }

    fun showManageTagsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_manage_tags, null)
        val etTags = dialogView.findViewById<EditText>(R.id.etManagedTags)
        val etSubcategories = dialogView.findViewById<EditText>(R.id.etManagedSubcategories)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelManageTags)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveManageTags)

        etTags.setText(getKnownTags().joinToString(", "))
        etSubcategories.setText(getKnownSubcategories().joinToString(", "))

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            savedTagsProvider().apply {
                clear()
                addAll(parseTags(etTags.text.toString()))
            }
            savedSubcategoriesProvider().apply {
                clear()
                addAll(parseSubcategories(etSubcategories.text.toString()))
            }
            saveData()
            updateSettingsPageValues()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun showTagBottomSheet(
        options: List<String>,
        checked: BooleanArray,
        selected: MutableSet<String>,
        target: EditText
    ) {
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(14), context.dpToPx(14), context.dpToPx(14), context.dpToPx(14))
            background = context.getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(context).apply {
            text = context.getString(R.string.choose_tags)
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })

        options.forEachIndexed { index, option ->
            content.addView(CheckBox(context).apply {
                text = option
                isChecked = checked.getOrNull(index) == true
                buttonTintList = ColorStateList.valueOf(context.getColor(R.color.calendar_accent))
                setTextColor(context.getColor(R.color.text_primary))
                textSize = 14f
                setPadding(0, context.dpToPx(4), 0, context.dpToPx(4))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selected.add(option)
                    } else {
                        selected.removeAll { it.equals(option, ignoreCase = true) }
                    }
                }
            })
        }

        val dialog = BottomSheetDialog(context)
        val actions = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, context.dpToPx(10), 0, 0)
        }
        actions.addView(Button(context).apply {
            text = context.getString(R.string.cancel)
            isAllCaps = false
            background = context.getDrawable(R.drawable.bg_button_light)
            backgroundTintList = null
            setTextColor(context.getColor(R.color.button_text_dark))
            layoutParams = LinearLayout.LayoutParams(0, context.dpToPx(48), 1f)
            setOnClickListener { dialog.dismiss() }
        })
        actions.addView(Button(context).apply {
            text = context.getString(R.string.save_changes)
            isAllCaps = false
            background = context.getDrawable(R.drawable.bg_button_dark)
            backgroundTintList = null
            setTextColor(context.getColor(R.color.button_text_light))
            layoutParams = LinearLayout.LayoutParams(0, context.dpToPx(48), 1.2f).apply {
                marginStart = context.dpToPx(8)
            }
            setOnClickListener {
                val tags = selected.toList().distinctBy { it.lowercase(Locale.getDefault()) }
                target.setText(tagsToInput(tags))
                savedTagsProvider().addAll(tags)
                dialog.dismiss()
            }
        })
        content.addView(actions)
        val root = ScrollView(context).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun getPopularTags(): List<String> {
        return listOf(
            context.getString(R.string.tag_work),
            context.getString(R.string.tag_family),
            context.getString(R.string.tag_health),
            context.getString(R.string.tag_urgent),
            context.getString(R.string.tag_cash),
            context.getString(R.string.tag_online),
            context.getString(R.string.tag_refund),
            context.getString(R.string.tag_trip)
        )
    }

    private fun getPopularSubcategories(): List<String> {
        return listOf(
            context.getString(R.string.subcategory_groceries),
            context.getString(R.string.subcategory_restaurants),
            context.getString(R.string.subcategory_fuel),
            context.getString(R.string.subcategory_taxi),
            context.getString(R.string.subcategory_rent),
            context.getString(R.string.subcategory_utilities),
            context.getString(R.string.subcategory_insurance),
            context.getString(R.string.subcategory_installment),
            context.getString(R.string.subcategory_medicine),
            context.getString(R.string.subcategory_clothes),
            context.getString(R.string.subcategory_gifts),
            context.getString(R.string.subcategory_transfer)
        )
    }

    private fun parseSubcategories(value: String): List<String> {
        return value
            .split(",", "|", ";")
            .mapNotNull(::normalizeOptionalText)
            .distinctBy { it.lowercase(Locale.getDefault()) }
    }
}
