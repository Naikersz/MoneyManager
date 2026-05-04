package com.example.moneymanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

class CalendarPieMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val oval = RectF()
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var incomeAmount = 0.0
    private var expenseAmount = 0.0
    private var isSelected = false

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setData(incomeAmount: Double, expenseAmount: Double, isSelected: Boolean, isToday: Boolean) {
        this.incomeAmount = incomeAmount
        this.expenseAmount = expenseAmount
        this.isSelected = isSelected
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = dp(26f).toInt()
        val width = resolveSize(defaultSize, widthMeasureSpec)
        val height = resolveSize(defaultSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val inset = dp(4f)
        oval.set(inset, inset, size - inset, size - inset)

        val total = incomeAmount + expenseAmount

        if (total <= 0.0) {
            fillPaint.color = Color.TRANSPARENT
            canvas.drawOval(oval, fillPaint)
            return
        }

        shadowPaint.color = ContextCompat.getColor(context, R.color.calendar_event_shadow_fill)
        shadowPaint.setShadowLayer(
            dp(3f),
            0f,
            dp(1f),
            ContextCompat.getColor(context, R.color.calendar_event_shadow)
        )
        canvas.drawOval(oval, shadowPaint)
        shadowPaint.clearShadowLayer()

        if (isSelected) {
            fillPaint.color = ContextCompat.getColor(context, R.color.calendar_selected_fill)
            canvas.drawOval(oval, fillPaint)
        }

        val incomeSweep = ((incomeAmount / total) * 360.0).toFloat().coerceIn(0f, 360f)
        val expenseSweep = 360f - incomeSweep

        fillPaint.color = ContextCompat.getColor(context, R.color.calendar_income_arc)
        canvas.drawArc(oval, -90f, incomeSweep, true, fillPaint)

        if (expenseSweep > 0f) {
            fillPaint.color = ContextCompat.getColor(context, R.color.calendar_expense_arc)
            canvas.drawArc(oval, -90f + incomeSweep, expenseSweep, true, fillPaint)
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
