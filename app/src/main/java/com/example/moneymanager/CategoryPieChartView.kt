package com.example.moneymanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CategoryPieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(
        val label: String,
        val amount: Double,
        val color: Int,
        val valueText: String = ""
    )

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val labelBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val labelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val bounds = RectF()
    private val labelBounds = RectF()
    private var slices: List<Slice> = emptyList()
    private var centerText: String = ""

    fun setSlices(newSlices: List<Slice>) {
        slices = newSlices.filter { it.amount > 0.0 }
        invalidate()
    }

    fun setCenterText(text: String) {
        centerText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = minOf(width, height).toFloat()
        if (size <= 0f) return

        val radius = size * 0.42f
        val centerX = width / 2f
        val centerY = height / 2f
        val strokeWidth = (size * 0.13f).coerceIn(dpToPx(16).toFloat(), dpToPx(26).toFloat())
        arcPaint.strokeWidth = strokeWidth
        trackPaint.strokeWidth = strokeWidth
        bounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        trackPaint.color = context.getColor(R.color.divider_soft)
        canvas.drawCircle(centerX, centerY, radius, trackPaint)

        if (slices.isEmpty()) {
            drawCenterText(canvas, centerX, centerY, radius)
            return
        }

        val total = slices.sumOf { it.amount }.takeIf { it > 0.0 } ?: return
        val gapAngle = if (slices.size > 1) {
            2.4f.coerceAtMost(48f / slices.size)
        } else {
            0f
        }
        var startAngle = -90f
        slices.forEach { slice ->
            val rawSweep = (slice.amount / total * 360.0).toFloat()
            val sweep = if (gapAngle == 0f) {
                rawSweep
            } else {
                (rawSweep - gapAngle).coerceAtLeast(rawSweep * 0.45f)
            }
            arcPaint.color = slice.color
            canvas.drawArc(bounds, startAngle + gapAngle / 2f, sweep, false, arcPaint)
            startAngle += rawSweep
        }

        drawCenterText(canvas, centerX, centerY, radius)
        drawFloatingLabels(canvas, centerX, centerY, radius, total)
    }

    private fun drawFloatingLabels(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, total: Double) {
        var startAngle = -90f
        val labelSlices = slices.take(3).map { it.label }.toSet()
        slices.forEach { slice ->
            val rawSweep = (slice.amount / total * 360.0).toFloat()
            if (slice.label in labelSlices && rawSweep >= 16f) {
                drawFloatingLabel(canvas, slice, centerX, centerY, radius, startAngle + rawSweep / 2f)
            }
            startAngle += rawSweep
        }
    }

    private fun drawFloatingLabel(canvas: Canvas, slice: Slice, centerX: Float, centerY: Float, radius: Float, angle: Float) {
        val text = slice.valueText.ifBlank { slice.amount.toInt().toString() }
        val radians = Math.toRadians(angle.toDouble())
        val labelX = centerX + cos(radians).toFloat() * radius * 0.90f
        val labelY = centerY + sin(radians).toFloat() * radius * 0.90f
        labelTextPaint.textSize = dpToPx(10).toFloat()
        labelTextPaint.color = readableTextColor(slice.color)

        val horizontalPadding = dpToPx(7).toFloat()
        val verticalPadding = dpToPx(3).toFloat()
        val textWidth = labelTextPaint.measureText(text)
        val fontMetrics = labelTextPaint.fontMetrics
        val labelWidth = textWidth + horizontalPadding * 2f
        val labelHeight = fontMetrics.descent - fontMetrics.ascent + verticalPadding * 2f
        val halfWidth = labelWidth / 2f
        val halfHeight = labelHeight / 2f
        val safeX = labelX.coerceIn(halfWidth + dpToPx(6), width - halfWidth - dpToPx(6))
        val safeY = labelY.coerceIn(halfHeight + dpToPx(6), height - halfHeight - dpToPx(6))

        labelBounds.set(safeX - halfWidth, safeY - halfHeight, safeX + halfWidth, safeY + halfHeight)
        labelBackgroundPaint.color = withAlpha(slice.color, 160)
        canvas.drawRoundRect(labelBounds, dpToPx(7).toFloat(), dpToPx(7).toFloat(), labelBackgroundPaint)
        canvas.drawText(text, safeX, safeY - (fontMetrics.ascent + fontMetrics.descent) / 2f, labelTextPaint)
    }

    private fun drawCenterText(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        if (centerText.isNotBlank()) {
            centerTextPaint.color = context.getColor(R.color.text_primary)
            var textSize = dpToPx(18).toFloat()
            val maxWidth = radius * 1.45f
            centerTextPaint.textSize = textSize
            while (textSize > dpToPx(12) && centerTextPaint.measureText(centerText) > maxWidth) {
                textSize -= 1f
                centerTextPaint.textSize = textSize
            }
            centerTextPaint.textSize = textSize
            canvas.drawText(centerText, centerX, centerY + dpToPx(6), centerTextPaint)
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun readableTextColor(color: Int): Int {
        val luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255.0
        return if (luminance > 0.56) {
            context.getColor(R.color.home_dark_text)
        } else {
            context.getColor(R.color.text_primary)
        }
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
