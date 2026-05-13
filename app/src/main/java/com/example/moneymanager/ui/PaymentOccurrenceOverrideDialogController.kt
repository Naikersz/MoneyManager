package com.example.moneymanager.ui

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.moneymanager.CalendarEvent
import com.example.moneymanager.PaymentOccurrenceOverride
import com.example.moneymanager.R
import com.example.moneymanager.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class PaymentOccurrenceOverrideDialogController(
    private val context: Context,
    private val formatDisplayDate: (String?) -> String,
    private val showDatePicker: (String?, (String) -> Unit) -> Unit,
    private val onSaveOverride: (CalendarEvent, String, String, Double, Double) -> Unit,
    private val onResetOverride: (CalendarEvent, String) -> Unit,
    private val onEditSeries: (CalendarEvent) -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit,
    private val focusAmountInput: (EditText) -> Unit
) {
    fun show(
        event: CalendarEvent,
        originalDate: String,
        currentOverride: PaymentOccurrenceOverride?,
        baseAmount: Double
    ) {
        var selectedDate = currentOverride?.date ?: event.occurrenceDate ?: originalDate
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(18), context.dpToPx(18), context.dpToPx(18), context.dpToPx(18))
            background = context.getDrawable(R.drawable.bg_home_balance_card)
        }

        content.addView(TextView(context).apply {
            text = context.getString(R.string.edit_payment_occurrence)
            textSize = 20f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(context.getColor(R.color.text_primary))
        })
        content.addView(TextView(context).apply {
            text = event.name
            textSize = 13f
            setTextColor(context.getColor(R.color.text_secondary))
            setPadding(0, context.dpToPx(4), 0, context.dpToPx(12))
        })

        val etAmount = EditText(context).apply {
            setText((currentOverride?.amount ?: event.amount.takeIf { it > 0.0 } ?: baseAmount).toString())
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = context.getString(R.string.amount)
            setSingleLine(true)
            setTextColor(context.getColor(R.color.text_primary))
            setHintTextColor(context.getColor(R.color.text_hint))
            highlightColor = context.getColor(R.color.calendar_accent_soft)
            background = context.getDrawable(R.drawable.bg_input_surface)
            setPadding(context.dpToPx(14), 0, context.dpToPx(14), 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dpToPx(48)
            )
        }
        content.addView(etAmount)

        val btnDate = Button(context).apply {
            isAllCaps = false
            textSize = 14f
            setTextColor(context.getColor(R.color.text_primary))
            background = context.getDrawable(R.drawable.bg_input_surface)
            backgroundTintList = null
            minHeight = 0
            minWidth = 0
            includeFontPadding = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dpToPx(48)
            ).apply {
                topMargin = context.dpToPx(10)
            }
        }
        updateDateButton(btnDate, selectedDate)
        content.addView(btnDate)

        val btnSave = createDialogButton(context.getString(R.string.save_changes), dark = true)
        val btnReset = createDialogButton(context.getString(R.string.reset_payment_occurrence), dark = false)
        val btnEditSeries = createDialogButton(context.getString(R.string.edit_payment_series), dark = false)
        val btnClose = createDialogButton(context.getString(R.string.close), dark = false)
        content.addView(btnSave)
        content.addView(btnReset)
        content.addView(btnEditSeries)
        content.addView(btnClose)

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(content)

        btnDate.setOnClickListener {
            showDatePicker(selectedDate) { pickedDate ->
                selectedDate = pickedDate
                updateDateButton(btnDate, selectedDate)
            }
        }
        btnSave.setOnClickListener {
            val amount = parsePositiveAmount(etAmount) ?: return@setOnClickListener
            onSaveOverride(event, originalDate, selectedDate, amount, baseAmount)
            dialog.dismiss()
        }
        btnReset.setOnClickListener {
            onResetOverride(event, originalDate)
            dialog.dismiss()
        }
        btnEditSeries.setOnClickListener {
            dialog.dismiss()
            onEditSeries(event)
        }
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
        focusAmountInput(etAmount)
    }

    private fun createDialogButton(textValue: String, dark: Boolean): Button {
        return Button(context).apply {
            text = textValue
            isAllCaps = false
            textSize = 14f
            setTextColor(context.getColor(if (dark) R.color.button_text_light else R.color.button_text_dark))
            background = context.getDrawable(if (dark) R.drawable.bg_button_dark else R.drawable.bg_button_light)
            backgroundTintList = null
            minHeight = 0
            minWidth = 0
            includeFontPadding = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dpToPx(48)
            ).apply {
                topMargin = context.dpToPx(10)
            }
        }
    }

    private fun updateDateButton(button: Button, dateValue: String?) {
        button.text = if (dateValue.isNullOrBlank()) {
            context.getString(R.string.select_charge_date)
        } else {
            context.getString(R.string.selected_date, formatDisplayDate(dateValue))
        }
    }

    private fun parsePositiveAmount(input: EditText): Double? {
        val amount = input.text.toString().trim().replace(',', '.').toDoubleOrNull()
        return if (amount == null || amount <= 0.0) {
            input.error = context.getString(R.string.invalid_amount)
            null
        } else {
            input.error = null
            amount
        }
    }
}
