package com.example.moneymanager.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.moneymanager.CalendarEvent
import com.example.moneymanager.ItemType
import com.example.moneymanager.R
import com.example.moneymanager.calendarEventSignedAmount
import com.example.moneymanager.canUpdateCalendarPaymentStatus
import com.example.moneymanager.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class CalendarDayDialogController(
    private val context: Context,
    private val formatDisplayDate: (String?) -> String,
    private val formatSignedAmount: (Double) -> String,
    private val onEventClick: (CalendarEvent) -> Unit,
    private val onMarkPaid: (CalendarEvent) -> Unit,
    private val onSkip: (CalendarEvent) -> Unit,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun show(dateIso: String, events: List<CalendarEvent>) {
        if (events.isEmpty()) return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calendar_day, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCalendarDayDialogTitle)
        val layoutEvents = dialogView.findViewById<LinearLayout>(R.id.layoutCalendarDayDialogEvents)
        val layoutActions = dialogView.findViewById<LinearLayout>(R.id.layoutCalendarPaymentActions)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseCalendarDayDialog)
        val dialog = BottomSheetDialog(context)

        tvTitle.text = context.getString(R.string.calendar_day_details, formatDisplayDate(dateIso))
        layoutEvents.removeAllViews()
        events.forEach { event ->
            layoutEvents.addView(
                createEventRow(
                    event = event,
                    onClick = {
                        dialog.dismiss()
                        onEventClick(event)
                    },
                    onMarkPaid = {
                        onMarkPaid(event)
                        dialog.dismiss()
                    },
                    onSkip = {
                        onSkip(event)
                        dialog.dismiss()
                    }
                )
            )
        }
        layoutActions.visibility = View.GONE

        dialog.setContentView(dialogView)
        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        styleBottomSheet(dialog)
    }

    private fun createEventRow(
        event: CalendarEvent,
        onClick: () -> Unit,
        onMarkPaid: () -> Unit,
        onSkip: () -> Unit
    ): View {
        val signedAmount = calendarEventSignedAmount(event)
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(12), context.dpToPx(10), context.dpToPx(12), context.dpToPx(10))
            background = createEventRowBackground(event)
            isClickable = event.sourceIndex >= 0
            isFocusable = event.sourceIndex >= 0
            alpha = when {
                event.isSkipped -> 0.54f
                event.isForecast -> 0.72f
                else -> 1f
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = context.dpToPx(8)
            }
            if (event.sourceIndex >= 0) {
                setOnClickListener { onClick() }
            }
        }

        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        topRow.addView(TextView(context).apply {
            text = event.name
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(context.getColor(R.color.text_primary))
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        topRow.addView(TextView(context).apply {
            text = formatSignedAmount(signedAmount)
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(
                context.getColor(
                    when {
                        signedAmount > 0.0 -> R.color.amount_positive
                        signedAmount < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
        })

        row.addView(topRow)
        row.addView(TextView(context).apply {
            text = buildEventMeta(event)
            textSize = 12f
            setTextColor(context.getColor(if (event.isOverdue) R.color.amount_negative else R.color.text_secondary))
            setPadding(0, context.dpToPx(5), 0, 0)
        })

        if (canUpdateCalendarPaymentStatus(event)) {
            row.addView(createEventActionsRow(onMarkPaid, onSkip))
        }
        return row
    }

    private fun createEventRowBackground(event: CalendarEvent): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dpToPx(14).toFloat()
            setColor(context.getColor(R.color.home_panel))
            setStroke(context.dpToPx(1), context.getColor(if (event.isOverdue) R.color.amount_negative else R.color.home_line))
        }
    }

    private fun createEventActionsRow(onMarkPaid: () -> Unit, onSkip: () -> Unit): View {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, context.dpToPx(10), 0, 0)
            addView(Button(context).apply {
                text = context.getString(R.string.mark_paid)
                isAllCaps = false
                textSize = 12f
                minHeight = 0
                minWidth = 0
                setTextColor(context.getColor(R.color.button_text_light))
                background = context.getDrawable(R.drawable.bg_button_dark)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(0, context.dpToPx(38), 1f).apply {
                    marginEnd = context.dpToPx(6)
                }
                setOnClickListener { onMarkPaid() }
            })
            addView(Button(context).apply {
                text = context.getString(R.string.skip_payment)
                isAllCaps = false
                textSize = 12f
                minHeight = 0
                minWidth = 0
                setTextColor(context.getColor(R.color.button_text_dark))
                background = context.getDrawable(R.drawable.bg_button_light)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(0, context.dpToPx(38), 1f).apply {
                    marginStart = context.dpToPx(6)
                }
                setOnClickListener { onSkip() }
            })
        }
    }

    private fun buildEventMeta(event: CalendarEvent): String {
        return listOf(
            getEventTypeLabel(event.type),
            event.detail,
            getEventStatusLabel(event)
        ).filter { it.isNotBlank() }.joinToString(" \u00B7 ")
    }

    private fun getEventTypeLabel(type: ItemType): String {
        return when (type) {
            ItemType.INCOME -> context.getString(R.string.income)
            ItemType.SUBSCRIPTION -> context.getString(R.string.subscription)
            ItemType.RECURRING_EXPENSE -> context.getString(R.string.recurring_expense)
            ItemType.EXPENSE -> context.getString(R.string.action_expense)
            ItemType.TRANSFER -> context.getString(R.string.transfer)
        }
    }

    private fun getEventStatusLabel(event: CalendarEvent): String {
        val status = when {
            event.isSkipped -> context.getString(R.string.payment_status_skipped)
            event.isPaid -> context.getString(R.string.payment_status_paid)
            event.isOverdue -> context.getString(R.string.calendar_overdue)
            event.isForecast -> context.getString(R.string.calendar_forecast)
            else -> ""
        }
        return status.takeUnless { it.isNotBlank() && event.detail.contains(it) }.orEmpty()
    }
}
