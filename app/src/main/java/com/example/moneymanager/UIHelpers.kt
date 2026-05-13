package com.example.moneymanager

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

object DialogHelper {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}

fun Context.dpToPx(value: Int): Int {
    return (value * resources.displayMetrics.density).toInt()
}

fun View.addPressAnimation() {
    setOnTouchListener { view, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                view.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80L)
                    .start()
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120L)
                    .start()
            }
        }
        false
    }
}
