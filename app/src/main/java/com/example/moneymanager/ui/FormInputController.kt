package com.example.moneymanager.ui

import android.app.DatePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.example.moneymanager.R
import com.example.moneymanager.UiDateFormatter
import java.util.Calendar

internal class FormInputController(
    private val context: Context,
    private val uiDateFormatter: UiDateFormatter
) {
    fun focusAmountInput(input: EditText) {
        input.post {
            input.requestFocus()
            input.selectAll()
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun parsePositiveAmount(input: EditText): Double? {
        val amount = input.text.toString().trim().replace(',', '.').toDoubleOrNull()
        return if (amount == null || amount <= 0.0) {
            input.error = context.getString(R.string.invalid_amount)
            null
        } else {
            input.error = null
            amount
        }
    }

    fun showDatePicker(initialDate: String?, onDateSelected: (String) -> Unit) {
        val calendar = uiDateFormatter.parseIsoDate(initialDate) ?: Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                onDateSelected(uiDateFormatter.isoDateFormatter().format(pickedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    fun updateDateButton(button: Button, dateValue: String?, emptyTextRes: Int) {
        button.text = if (dateValue.isNullOrBlank()) {
            context.getString(emptyTextRes)
        } else {
            context.getString(R.string.selected_date, uiDateFormatter.formatDisplayDate(dateValue))
        }
    }

    fun updateDateShortcutButtons(selectedDate: String?, vararg shortcuts: Pair<Button, Int>) {
        shortcuts.forEach { (button, offsetDays) ->
            val isSelected = selectedDate == uiDateFormatter.isoDateWithOffset(offsetDays)
            button.background = context.getDrawable(if (isSelected) R.drawable.bg_tab_chip_selected else R.drawable.bg_button_light)
            button.backgroundTintList = null
            button.setTextColor(context.getColor(if (isSelected) R.color.home_dark_text else R.color.button_text_dark))
        }
    }
}
