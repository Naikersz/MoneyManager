package com.example.moneymanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import kotlin.math.max

class MonthlyTrendChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class MonthPoint(
        val label: String,
        val income: Double,
        val outgoing: Double,
        val net: Double,
        val incomeText: String = "",
        val outgoingText: String = "",
        val netText: String = ""
    )

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1).toFloat()
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1).toFloat()
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(2).toFloat()
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val dotStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(2).toFloat()
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = dpToPx(11).toFloat()
    }
    private val valueTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.RIGHT
        textSize = dpToPx(10).toFloat()
    }
    private val tooltipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val tooltipTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = dpToPx(11).toFloat()
    }
    private val barBounds = RectF()
    private val tooltipBounds = RectF()
    private val linePath = Path()
    private var points: List<MonthPoint> = emptyList()
    private var selectedIndex: Int = -1

    fun setPoints(newPoints: List<MonthPoint>) {
        points = newPoints
        invalidate()
    }

    fun setSelectedIndex(index: Int) {
        selectedIndex = index
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return

        val left = dpToPx(42).toFloat()
        val right = width - dpToPx(10).toFloat()
        val top = dpToPx(76).toFloat()
        val bottom = height - dpToPx(30).toFloat()
        val baseline = top + (bottom - top) / 2f
        val halfHeight = (bottom - top) / 2f - dpToPx(8)

        if (points.isEmpty()) return

        val maxValue = points.fold(1.0) { current, point ->
            max(current, max(max(point.income, point.outgoing), abs(point.net)))
        }
        val labelColor = context.getColor(R.color.text_secondary)
        val gridColor = context.getColor(R.color.divider_soft)
        gridPaint.color = gridColor
        axisPaint.color = gridColor
        valueTextPaint.color = labelColor
        drawHorizontalGuide(canvas, left, right, top, formatCompactValue(maxValue))
        drawHorizontalGuide(canvas, left, right, baseline, "0")
        drawHorizontalGuide(canvas, left, right, bottom, "-${formatCompactValue(maxValue)}")

        val groupWidth = (right - left) / points.size
        val barWidth = (groupWidth * 0.18f).coerceAtMost(dpToPx(12).toFloat())
        val incomeColor = context.getColor(R.color.amount_positive)
        val outgoingColor = context.getColor(R.color.amount_negative)
        val netColor = context.getColor(R.color.budget_warning)
        val selected = selectedIndex.takeIf { it in points.indices } ?: points.lastIndex

        points.forEachIndexed { index, point ->
            val centerX = left + groupWidth * index + groupWidth / 2f
            val incomeHeight = scaledHeight(point.income, maxValue, halfHeight)
            val outgoingHeight = scaledHeight(point.outgoing, maxValue, halfHeight)
            val incomeTop = baseline - incomeHeight
            val outgoingBottom = baseline + outgoingHeight

            if (incomeHeight > 0f) {
                barPaint.color = incomeColor
                barBounds.set(centerX - barWidth - dpToPx(2), incomeTop, centerX - dpToPx(2), baseline)
                canvas.drawRoundRect(barBounds, dpToPx(4).toFloat(), dpToPx(4).toFloat(), barPaint)
            }

            if (outgoingHeight > 0f) {
                barPaint.color = outgoingColor
                barBounds.set(centerX + dpToPx(2), baseline, centerX + barWidth + dpToPx(2), outgoingBottom)
                canvas.drawRoundRect(barBounds, dpToPx(4).toFloat(), dpToPx(4).toFloat(), barPaint)
            }

            textPaint.color = labelColor
            textPaint.typeface = if (index == selected) {
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                Typeface.DEFAULT
            }
            canvas.drawText(point.label, centerX, height - dpToPx(8).toFloat(), textPaint)
        }
        textPaint.typeface = Typeface.DEFAULT

        linePath.reset()
        points.forEachIndexed { index, point ->
            val centerX = left + groupWidth * index + groupWidth / 2f
            val y = baseline - (point.net / maxValue * halfHeight).toFloat()
            if (index == 0) {
                linePath.moveTo(centerX, y)
            } else {
                linePath.lineTo(centerX, y)
            }
        }

        linePaint.color = netColor
        canvas.drawPath(linePath, linePaint)
        dotPaint.color = netColor
        dotStrokePaint.color = context.getColor(R.color.card_surface)
        points.forEachIndexed { index, point ->
            val centerX = left + groupWidth * index + groupWidth / 2f
            val y = baseline - (point.net / maxValue * halfHeight).toFloat()
            val dotRadius = if (index == selected) dpToPx(4).toFloat() else dpToPx(3).toFloat()
            canvas.drawCircle(centerX, y, dotRadius + dpToPx(1), dotStrokePaint)
            canvas.drawCircle(centerX, y, dotRadius, dotPaint)
        }
        drawTooltip(canvas, points[selected], left + groupWidth * selected + groupWidth / 2f, incomeColor, outgoingColor)
    }

    private fun drawHorizontalGuide(canvas: Canvas, left: Float, right: Float, y: Float, label: String) {
        val paint = if (label == "0") axisPaint else gridPaint
        canvas.drawLine(left, y, right, y, paint)
        canvas.drawText(label, left - dpToPx(6), y + dpToPx(4), valueTextPaint)
    }

    private fun scaledHeight(value: Double, maxValue: Double, halfHeight: Float): Float {
        if (value <= 0.0) return 0f
        return (value / maxValue * halfHeight).toFloat().coerceAtLeast(dpToPx(3).toFloat())
    }

    private fun formatCompactValue(value: Double): String {
        val absValue = abs(value)
        return when {
            absValue >= 1_000_000.0 -> String.format(java.util.Locale.getDefault(), "%.1fM", absValue / 1_000_000.0)
            absValue >= 1_000.0 -> String.format(java.util.Locale.getDefault(), "%.1fk", absValue / 1_000.0)
            else -> absValue.toInt().toString()
        }
    }

    private fun drawTooltip(
        canvas: Canvas,
        point: MonthPoint,
        centerX: Float,
        incomeColor: Int,
        outgoingColor: Int
    ) {
        val titleText = "${point.label}  ${point.netText}"
        val incomeText = "${context.getString(R.string.analytics_legend_income)} ${point.incomeText}"
        val outgoingText = "${context.getString(R.string.analytics_legend_outgoing)} ${point.outgoingText}"
        tooltipTextPaint.typeface = Typeface.DEFAULT_BOLD
        val titleWidth = tooltipTextPaint.measureText(titleText)
        tooltipTextPaint.typeface = Typeface.DEFAULT
        val maxLineWidth = max(
            titleWidth,
            max(tooltipTextPaint.measureText(incomeText), tooltipTextPaint.measureText(outgoingText))
        )
        val padding = dpToPx(10).toFloat()
        val minTooltipWidth = dpToPx(126).toFloat()
        val maxTooltipWidth = (width - dpToPx(18).toFloat()).coerceAtLeast(minTooltipWidth)
        val tooltipWidth = (maxLineWidth + padding * 2f).coerceIn(minTooltipWidth, maxTooltipWidth)
        val tooltipHeight = dpToPx(54).toFloat()
        val minLeft = dpToPx(6).toFloat()
        val maxLeft = (width - tooltipWidth - dpToPx(6).toFloat()).coerceAtLeast(minLeft)
        val left = (centerX - tooltipWidth / 2f).coerceIn(minLeft, maxLeft)
        val tooltipTop = dpToPx(8).toFloat()

        tooltipBounds.set(left, tooltipTop, left + tooltipWidth, tooltipTop + tooltipHeight)
        tooltipPaint.color = context.getColor(R.color.chart_tooltip_background)
        canvas.drawRoundRect(tooltipBounds, dpToPx(10).toFloat(), dpToPx(10).toFloat(), tooltipPaint)

        val textLeft = left + padding
        var textY = tooltipTop + dpToPx(17)
        tooltipTextPaint.typeface = Typeface.DEFAULT_BOLD
        tooltipTextPaint.color = context.getColor(R.color.text_primary)
        canvas.drawText(titleText, textLeft, textY, tooltipTextPaint)
        tooltipTextPaint.typeface = Typeface.DEFAULT

        textY += dpToPx(16)
        tooltipTextPaint.color = incomeColor
        canvas.drawText(incomeText, textLeft, textY, tooltipTextPaint)
        textY += dpToPx(14)
        tooltipTextPaint.color = outgoingColor
        canvas.drawText(outgoingText, textLeft, textY, tooltipTextPaint)
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
