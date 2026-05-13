package com.example.moneymanager.ui

import android.content.Context
import com.example.moneymanager.R
import java.util.Locale

internal class CategoryOptionsController(
    private val context: Context,
    private val categoriesProvider: () -> List<String>,
    private val favoriteCategoriesProvider: () -> Set<String>
) {
    fun getIncomeTypeOptions(): List<String> {
        return listOf(
            context.getString(R.string.income_salary),
            context.getString(R.string.income_freelance),
            context.getString(R.string.income_bonus),
            context.getString(R.string.income_other)
        )
    }

    fun getIncomeTypeKey(value: String): String {
        return when (value.trim().lowercase(Locale.getDefault())) {
            "salary", "gehalt", "\u0437\u0430\u0440\u043f\u043b\u0430\u0442\u0430" -> "salary"
            "freelance", "\u0444\u0440\u0438\u043b\u0430\u043d\u0441" -> "freelance"
            "bonus", "\u043f\u0440\u0435\u043c\u0438\u044f" -> "bonus"
            "other", "sonstiges", "\u0434\u0440\u0443\u0433\u043e\u0435" -> "other"
            else -> value
        }
    }

    fun getIncomeTypeDisplayName(value: String): String {
        return when (getIncomeTypeKey(value)) {
            "salary" -> context.getString(R.string.income_salary)
            "freelance" -> context.getString(R.string.income_freelance)
            "bonus" -> context.getString(R.string.income_bonus)
            "other" -> context.getString(R.string.income_other)
            else -> value
        }
    }

    fun getIncomePeriodOptions(): List<String> {
        return listOf(
            context.getString(R.string.once),
            context.getString(R.string.monthly)
        )
    }

    fun getCategoryOptions(): MutableList<String> {
        val categories = categoriesProvider()
        val favoriteCategories = favoriteCategoriesProvider()
        val favoriteOptions = categories
            .filter { favoriteCategories.contains(it) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
        val regularOptions = categories
            .filterNot { favoriteCategories.contains(it) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
        val popularOptions = getPopularCategoryNames()
            .filterNot { categories.contains(it) }

        return mutableListOf(context.getString(R.string.general_category)).apply {
            addAll(favoriteOptions)
            addAll(regularOptions)
            addAll(popularOptions)
        }
    }

    fun getCategoryDisplayName(category: String): String {
        return if (favoriteCategoriesProvider().contains(category)) {
            "${context.getString(R.string.favorite_marker)} $category"
        } else {
            category
        }
    }

    fun getPopularCategoryNames(): List<String> {
        return listOf(
            context.getString(R.string.category_food),
            context.getString(R.string.category_transport),
            context.getString(R.string.category_housing),
            context.getString(R.string.category_rent),
            context.getString(R.string.category_utilities),
            context.getString(R.string.category_health),
            context.getString(R.string.category_insurance),
            context.getString(R.string.category_credit_loans),
            context.getString(R.string.category_shopping),
            context.getString(R.string.category_entertainment),
            context.getString(R.string.category_internet_phone),
            context.getString(R.string.category_education),
            context.getString(R.string.category_travel),
            context.getString(R.string.category_transfers)
        ).distinct()
    }
}
