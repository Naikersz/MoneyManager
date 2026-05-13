package com.example.moneymanager.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.moneymanager.CalendarEvent
import com.example.moneymanager.ItemType
import com.example.moneymanager.R
import com.example.moneymanager.UiDateFormatter
import com.example.moneymanager.calendarEventPriority
import com.example.moneymanager.calendarEventsDelta
import com.example.moneymanager.dpToPx
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

internal class CalendarGridController(
    private val context: Context,
    private val gridCalendarDays: GridLayout,
    private val formatCompactSignedAmount: (Double) -> String,
    private val formatCompactCalendarAmount: (Double) -> String,
    private val formatDisplayDate: (String?) -> String,
    private val formatSignedAmount: (Double) -> String,
    private val onDateSelected: (String) -> Unit,
    private val onDateDoubleTap: (String) -> Unit,
    private val onDateLongClick: (String) -> Unit,
    private val onMonthSwipe: (Int) -> Unit
) {
    private val dateFormatter = UiDateFormatter()
    private var lastTapDateIso: String? = null
    private var lastTapAt: Long = 0L
    private var maxDayDelta = 1.0

    fun build(
        displayedMonth: Calendar,
        selectedDateIso: String,
        eventsByDate: Map<String, List<CalendarEvent>>
    ) {
        maxDayDelta = eventsByDate.values
            .map { events -> abs(calendarEventsDelta(events)) }
            .maxOrNull()
            ?.takeIf { it > 0.005 }
            ?: 1.0

        gridCalendarDays.removeAllViews()
        gridCalendarDays.columnCount = 7
        gridCalendarDays.rowCount = GridLayout.UNDEFINED

        val monthCalendar = displayedMonth.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayIndex = ((monthCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7)
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(6) { weekIndex ->
            val weekStart = weekIndex * 7
            val weekEnd = weekStart + 6
            val weekHasMonthDay = weekEnd >= firstDayIndex && weekStart < firstDayIndex + daysInMonth
            if (!weekHasMonthDay) return@repeat

            val weekDateIsos = mutableListOf<String>()
            repeat(7) { dayOffset ->
                val cellIndex = weekStart + dayOffset
                val view = if (cellIndex < firstDayIndex || cellIndex >= firstDayIndex + daysInMonth) {
                    createEmptyCalendarCell()
                } else {
                    val dayNumber = cellIndex - firstDayIndex + 1
                    val dateCalendar = displayedMonth.clone() as Calendar
                    dateCalendar.set(Calendar.DAY_OF_MONTH, dayNumber)
                    val dateIso = dateFormatter.isoDateFormatter().format(dateCalendar.time)
                    weekDateIsos.add(dateIso)
                    createCalendarDayCell(
                        dayNumber = dayNumber,
                        dateIso = dateIso,
                        events = eventsByDate[dateIso].orEmpty(),
                        selectedDateIso = selectedDateIso
                    )
                }
                gridCalendarDays.addView(view)
            }

            gridCalendarDays.addView(createCalendarWeekSummaryCell(weekDateIsos, eventsByDate))
        }
    }

    fun createSwipeTouchListener(): View.OnTouchListener {
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        var downX = 0f
        var downY = 0f
        var swipeHandled = false

        return View.OnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    swipeHandled = false
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (swipeHandled) {
                        return@OnTouchListener true
                    }

                    val deltaX = event.x - downX
                    val deltaY = event.y - downY
                    if (abs(deltaX) > touchSlop && abs(deltaX) > abs(deltaY)) {
                        swipeHandled = true
                        onMonthSwipe(if (deltaX > 0f) -1 else 1)
                        true
                    } else {
                        false
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val handled = swipeHandled
                    swipeHandled = false
                    handled
                }

                else -> false
            }
        }
    }

    private fun createEmptyCalendarCell(): View {
        return FrameLayout(context).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = context.dpToPx(34)
            alpha = 0f
            setOnTouchListener(createSwipeTouchListener())
        }
    }

    private fun createCalendarDayCell(
        dayNumber: Int,
        dateIso: String,
        events: List<CalendarEvent>,
        selectedDateIso: String
    ): View {
        val hasEvents = events.isNotEmpty()
        val isToday = dateIso == dateFormatter.isoDateFormatter().format(Date())
        val isSelected = dateIso == selectedDateIso
        val dayDelta = calendarEventsDelta(events)
        val isForecastOnly = hasEvents && events.all { it.isForecast }

        val container = LinearLayout(context).apply {
            layoutParams = createCalendarCellLayoutParams()
            minimumHeight = context.dpToPx(if (hasEvents) 42 else 34)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(context.dpToPx(2), context.dpToPx(3), context.dpToPx(2), context.dpToPx(3))
            background = createCalendarDayBackground(dayDelta, isToday, isSelected, isForecastOnly)
            contentDescription = buildCalendarDayContentDescription(dateIso, events, dayDelta)
            setOnTouchListener(createSwipeTouchListener())
            setOnClickListener {
                val now = System.currentTimeMillis()
                val isDoubleTap = lastTapDateIso == dateIso && now - lastTapAt <= 350L
                lastTapDateIso = dateIso
                lastTapAt = now
                onDateSelected(dateIso)
                if (isDoubleTap && hasEvents) {
                    onDateDoubleTap(dateIso)
                }
            }
            setOnLongClickListener {
                onDateLongClick(dateIso)
                true
            }
        }

        val dayLabel = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dpToPx(15)
            )
            text = dayNumber.toString()
            textSize = 11f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(
                context.getColor(
                    when {
                        isToday -> R.color.budget_warning
                        else -> R.color.text_primary
                    }
                )
            )
            typeface = if (isSelected || isToday) {
                android.graphics.Typeface.DEFAULT_BOLD
            } else {
                android.graphics.Typeface.DEFAULT
            }
        }

        val amountLabel = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dpToPx(if (hasEvents) 13 else 0)
            )
            text = formatCompactSignedAmount(dayDelta)
            textSize = 8f
            gravity = Gravity.CENTER
            includeFontPadding = false
            maxLines = 1
            visibility = if (hasEvents) View.VISIBLE else View.GONE
            setTextColor(
                context.getColor(
                    when {
                        dayDelta > 0.0 -> R.color.amount_positive
                        dayDelta < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
            alpha = if (isForecastOnly) 0.72f else 1f
        }

        val dotsRow = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                context.dpToPx(if (hasEvents) 6 else 0)
            )
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
            getCalendarDayDotTypes(events).take(3).forEach { type ->
                addView(createCalendarEventDot(type))
            }
            alpha = if (isForecastOnly) 0.62f else 1f
            visibility = if (hasEvents) View.VISIBLE else View.GONE
        }

        container.addView(dayLabel)
        container.addView(amountLabel)
        container.addView(dotsRow)
        return container
    }

    private fun createCalendarWeekSummaryCell(
        dateIsos: List<String>,
        eventsByDate: Map<String, List<CalendarEvent>>
    ): View {
        val weekDelta = dateIsos
            .flatMap { dateIso -> eventsByDate[dateIso].orEmpty() }
            .let(::calendarEventsDelta)
        val isZeroWeek = abs(weekDelta) < 0.005

        return TextView(context).apply {
            layoutParams = createCalendarWeekSummaryLayoutParams()
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
            visibility = if (isZeroWeek) View.GONE else View.VISIBLE
            text = if (isZeroWeek) {
                ""
            } else {
                context.getString(R.string.calendar_week_total, formatCompactCalendarAmount(weekDelta))
            }
            textSize = 10f
            includeFontPadding = false
            setPadding(context.dpToPx(4), 0, context.dpToPx(4), 0)
            setTextColor(
                context.getColor(
                    when {
                        weekDelta > 0.0 -> R.color.amount_positive
                        weekDelta < 0.0 -> R.color.amount_negative
                        else -> R.color.text_secondary
                    }
                )
            )
            alpha = if (isZeroWeek) 0f else 1f
        }
    }

    private fun createCalendarDayBackground(
        dayDelta: Double,
        isToday: Boolean,
        isSelected: Boolean,
        isForecastOnly: Boolean
    ): GradientDrawable {
        val alphaMultiplier = if (isForecastOnly) 0.52 else 1.0
        val fillColor = when {
            isSelected -> colorWithAlpha(context.getColor(R.color.calendar_accent), 92)
            isToday -> colorWithAlpha(context.getColor(R.color.budget_warning), 34)
            abs(dayDelta) < 0.005 -> colorWithAlpha(context.getColor(R.color.text_primary), 10)
            dayDelta > 0.0 -> colorWithAlpha(
                context.getColor(R.color.amount_positive),
                (calendarDayFillAlpha(dayDelta) * alphaMultiplier).toInt()
            )
            else -> colorWithAlpha(
                context.getColor(R.color.amount_negative),
                (calendarDayFillAlpha(dayDelta) * alphaMultiplier).toInt()
            )
        }
        val strokeWidth = when {
            isSelected -> context.dpToPx(2)
            isToday -> context.dpToPx(2)
            else -> 0
        }
        val strokeColor = when {
            isSelected -> context.getColor(R.color.calendar_accent)
            isToday -> context.getColor(R.color.budget_warning)
            else -> Color.TRANSPARENT
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dpToPx(7).toFloat()
            setColor(fillColor)
            setStroke(strokeWidth, strokeColor)
        }
    }

    private fun calendarDayFillAlpha(dayDelta: Double): Int {
        val ratio = (abs(dayDelta) / maxDayDelta).coerceIn(0.0, 1.0)
        return (28 + ratio * 74).toInt()
    }

    private fun colorWithAlpha(color: Int, alpha: Int): Int {
        return Color.argb(
            alpha.coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    private fun getCalendarDayDotTypes(events: List<CalendarEvent>): List<ItemType> {
        return events
            .map { it.type }
            .distinct()
            .sortedBy(::calendarEventPriority)
    }

    private fun createCalendarEventDot(type: ItemType): View {
        val dotColor = context.getColor(
            when (type) {
                ItemType.INCOME -> R.color.amount_positive
                ItemType.SUBSCRIPTION -> R.color.avatar_subscription_start
                ItemType.RECURRING_EXPENSE -> R.color.budget_warning
                ItemType.EXPENSE -> R.color.amount_negative
                ItemType.TRANSFER -> R.color.home_blue
            }
        )
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(context.dpToPx(5), context.dpToPx(5)).apply {
                marginStart = context.dpToPx(1)
                marginEnd = context.dpToPx(1)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(dotColor)
            }
        }
    }

    private fun buildCalendarDayContentDescription(
        dateIso: String,
        events: List<CalendarEvent>,
        dayDelta: Double
    ): String {
        val eventSummary = if (events.isEmpty()) {
            context.getString(R.string.calendar_no_events)
        } else {
            context.getString(R.string.calendar_events_summary, events.size)
        }
        return "${formatDisplayDate(dateIso)}. ${formatSignedAmount(dayDelta)}. $eventSummary"
    }

    private fun createCalendarCellLayoutParams(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(context.dpToPx(2), context.dpToPx(1), context.dpToPx(2), context.dpToPx(1))
        }
    }

    private fun createCalendarWeekSummaryLayoutParams(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = context.dpToPx(16)
            columnSpec = GridLayout.spec(0, 7, 1f)
            setMargins(context.dpToPx(2), 0, context.dpToPx(2), context.dpToPx(2))
        }
    }
}
