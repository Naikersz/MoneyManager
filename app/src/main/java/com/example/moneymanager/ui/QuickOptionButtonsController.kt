package com.example.moneymanager.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.moneymanager.R
import com.example.moneymanager.dpToPx

internal class QuickOptionButtonsController(
    private val context: Context
) {
    fun addButtons(
        container: LinearLayout,
        values: List<String>,
        selectedIndex: Int = 0,
        onSelected: (Int, String) -> Unit
    ) {
        container.removeAllViews()
        values.forEachIndexed { index, value ->
            val button = Button(context).apply {
                text = value
                isAllCaps = false
                textSize = 11f
                minWidth = 0
                minHeight = 0
                maxLines = 1
                includeFontPadding = false
                setPadding(context.dpToPx(10), 0, context.dpToPx(10), 0)
                setTextColor(context.getColor(if (index == selectedIndex) R.color.home_dark_text else R.color.text_primary))
                background = context.getDrawable(if (index == selectedIndex) R.drawable.bg_tab_chip_selected else R.drawable.bg_home_panel)
                backgroundTintList = null
                setOnClickListener {
                    onSelected(index, value)
                    addButtons(container, values, index, onSelected)
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    context.dpToPx(48)
                ).apply {
                    marginEnd = context.dpToPx(8)
                }
            }
            container.addView(button)
        }
    }
}
