package com.example.moneymanager.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout

internal class SubscriptionServiceSearchController(
    private val addQuickOptionButtons: (
        container: LinearLayout,
        values: List<String>,
        selectedIndex: Int,
        onSelected: (Int, String) -> Unit
    ) -> Unit
) {
    fun setup(etName: EditText, container: LinearLayout) {
        val services = getPopularServices()
        var updatingFromPreset = false

        fun render(query: String) {
            val normalizedQuery = query.trim()
            val filteredServices = services
                .filter { service ->
                    normalizedQuery.isEmpty() || service.contains(normalizedQuery, ignoreCase = true)
                }
                .take(14)
            val selectedIndex = filteredServices.indexOfFirst {
                it.equals(normalizedQuery, ignoreCase = true)
            }

            container.visibility = if (filteredServices.isEmpty()) View.GONE else View.VISIBLE
            addQuickOptionButtons(container, filteredServices, selectedIndex) { _, service ->
                updatingFromPreset = true
                etName.setText(service)
                etName.setSelection(service.length)
                updatingFromPreset = false
                render(service)
            }
        }

        render(etName.text.toString())
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!updatingFromPreset) {
                    render(s?.toString().orEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun getPopularServices(): List<String> {
        return listOf(
            "Netflix",
            "Spotify",
            "YouTube Premium",
            "Apple Music",
            "iCloud+",
            "Google One",
            "Microsoft 365",
            "Amazon Prime",
            "Disney+",
            "Max",
            "PlayStation Plus",
            "Xbox Game Pass",
            "Adobe",
            "Telegram Premium",
            "ChatGPT Plus",
            "GitHub Copilot",
            "Notion",
            "Figma",
            "Dropbox",
            "VPN",
            "Internet",
            "Mobile plan"
        ).distinct()
    }
}
