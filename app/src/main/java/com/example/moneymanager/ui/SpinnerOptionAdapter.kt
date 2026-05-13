package com.example.moneymanager.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.moneymanager.R

class SpinnerOptionAdapter(
    context: Context,
    values: List<String>
) : ArrayAdapter<String>(
    context,
    R.layout.item_spinner_selected,
    R.id.tvSpinnerText,
    values
) {
    init {
        setDropDownViewResource(R.layout.item_spinner_dropdown)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.findViewById<View>(R.id.spinnerDivider)?.visibility =
            if (position == count - 1) View.GONE else View.VISIBLE
        return view
    }
}
