package com.example.moneymanager.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.moneymanager.R
import com.google.android.material.snackbar.Snackbar

internal class UiFeedbackController(
    private val context: Context,
    private val rootProvider: () -> View,
    private val anchorProvider: () -> View?
) {
    fun setDetailsVisible(container: View, toggle: Button, visible: Boolean) {
        container.visibility = if (visible) View.VISIBLE else View.GONE
        toggle.text = context.getString(if (visible) R.string.hide_details else R.string.more_details)
    }

    fun showSavedSnackbar(undoAction: (() -> Unit)? = null) {
        val snackbarLength = if (undoAction != null) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
        val snackbar = Snackbar.make(rootProvider(), R.string.entry_saved, snackbarLength)
        snackbar.anchorView = anchorProvider()
        if (undoAction != null) {
            snackbar.setAction(R.string.undo) {
                undoAction()
            }
        }
        snackbar.show()
    }

    fun showToast(messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, messageRes, duration).show()
    }
}
