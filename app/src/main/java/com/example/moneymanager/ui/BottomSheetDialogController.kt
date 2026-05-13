package com.example.moneymanager.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import com.example.moneymanager.R
import com.example.moneymanager.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class BottomSheetDialogController(
    private val context: Context,
    private val styleBottomSheet: (BottomSheetDialog) -> Unit
) {
    fun showOption(options: List<String>, selectedIndex: Int = 0, onSelected: (Int) -> Unit) {
        if (options.size <= 1) return

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(14), context.dpToPx(14), context.dpToPx(14), context.dpToPx(14))
            background = context.getDrawable(R.drawable.bg_home_balance_card)
        }

        options.forEachIndexed { index, option ->
            val button = Button(context).apply {
                text = option
                isAllCaps = false
                textSize = 14f
                minHeight = 0
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                setPadding(context.dpToPx(16), 0, context.dpToPx(16), 0)
                setTextColor(context.getColor(if (index == selectedIndex) R.color.home_dark_text else R.color.text_primary))
                background = context.getDrawable(if (index == selectedIndex) R.drawable.bg_tab_chip_selected else R.drawable.bg_home_panel)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    context.dpToPx(48)
                ).apply {
                    if (index > 0) topMargin = context.dpToPx(8)
                }
            }
            content.addView(button)
        }

        val root = ScrollView(context).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = false
            addView(content)
        }

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(root)
        for (index in 0 until content.childCount) {
            content.getChildAt(index).setOnClickListener {
                dialog.dismiss()
                onSelected(index)
            }
        }
        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showChoice(title: String, options: List<String>, onSelected: (String) -> Unit) {
        if (options.isEmpty()) return

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(14), context.dpToPx(14), context.dpToPx(14), context.dpToPx(14))
            background = context.getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(context).apply {
            text = title
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })

        val dialog = BottomSheetDialog(context)
        options.forEach { option ->
            val button = Button(context).apply {
                text = option
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                minHeight = 0
                setPadding(context.dpToPx(16), 0, context.dpToPx(16), 0)
                setTextColor(context.getColor(R.color.text_primary))
                background = context.getDrawable(R.drawable.bg_home_panel)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    context.dpToPx(48)
                ).apply {
                    topMargin = context.dpToPx(8)
                }
            }
            button.setOnClickListener {
                onSelected(option)
                dialog.dismiss()
            }
            content.addView(button)
        }
        val root = ScrollView(context).apply {
            isFillViewport = false
            isVerticalScrollBarEnabled = true
            addView(content)
        }
        dialog.setContentView(root)
        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showDeleteConfirmation(message: String, onConfirm: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirm, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirmMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDeleteConfirm)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteDeleteConfirm)

        tvTitle.text = context.getString(R.string.delete_confirm_title)
        tvMessage.text = message

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showInfo() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_info, null)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseInfo)

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        styleBottomSheet(dialog)
    }

    fun showConfirmation(
        title: String,
        message: String,
        positiveText: String,
        onConfirm: () -> Unit
    ) {
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(context.dpToPx(14), context.dpToPx(14), context.dpToPx(14), context.dpToPx(14))
            background = context.getDrawable(R.drawable.bg_home_balance_card)
        }
        content.addView(TextView(context).apply {
            text = title
            setTextColor(context.getColor(R.color.text_primary))
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        content.addView(TextView(context).apply {
            text = message
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 14f
            setPadding(0, context.dpToPx(10), 0, context.dpToPx(12))
        })

        val actions = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val dialog = BottomSheetDialog(context)
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
            text = positiveText
            isAllCaps = false
            background = context.getDrawable(R.drawable.bg_button_dark)
            backgroundTintList = null
            setTextColor(context.getColor(R.color.button_text_light))
            layoutParams = LinearLayout.LayoutParams(0, context.dpToPx(48), 1.2f).apply {
                marginStart = context.dpToPx(8)
            }
            setOnClickListener {
                onConfirm()
                dialog.dismiss()
            }
        })
        content.addView(actions)
        dialog.setContentView(content)
        dialog.show()
        styleBottomSheet(dialog)
    }

    fun bindPicker(spinner: Spinner, options: List<String>, onSelected: (Int) -> Unit = {}) {
        spinner.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                showOption(options, spinner.selectedItemPosition) { index ->
                    spinner.setSelection(index)
                    onSelected(index)
                }
            }
            true
        }
    }
}
