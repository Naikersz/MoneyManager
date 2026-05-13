package com.example.moneymanager.ui

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class HomeMonthController(
    private val context: Context,
    private val displayedMonth: Calendar,
    private val monthLabelProvider: () -> TextView,
    private val onMonthChanged: () -> Unit
) {
    fun updateLabel() {
        val monthTitle = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(displayedMonth.time)
        monthLabelProvider().text = monthTitle.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    fun changeBy(offset: Int) {
        displayedMonth.add(Calendar.MONTH, offset)
        onMonthChanged()
    }

    fun showPicker() {
        val calendar = displayedMonth.clone() as Calendar
        DatePickerDialog(
            context,
            { _, year, month, _ ->
                displayedMonth.set(Calendar.YEAR, year)
                displayedMonth.set(Calendar.MONTH, month)
                displayedMonth.set(Calendar.DAY_OF_MONTH, 1)
                onMonthChanged()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
