package com.example.moneymanager.ui

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.R

class MainSectionsController(
    private val context: Context,
    private val viewPagerSections: ViewPager2,
    private val pages: List<View>,
    private val tabs: List<SectionTab>
) {
    private lateinit var sectionPagerAdapter: StaticPagesAdapter

    fun setup() {
        sectionPagerAdapter = StaticPagesAdapter(pages)
        viewPagerSections.adapter = sectionPagerAdapter
        viewPagerSections.offscreenPageLimit = pages.size
        viewPagerSections.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateSectionTabs(position)
            }
        })
        updateSectionTabs(viewPagerSections.currentItem)
    }

    private fun updateSectionTabs(position: Int) {
        tabs.forEachIndexed { index, tab ->
            styleSectionTab(tab, index == position)
        }
    }

    private fun styleSectionTab(tab: SectionTab, isSelected: Boolean) {
        tab.container.background = context.getDrawable(
            if (isSelected) R.drawable.bg_bottom_nav_item_selected else R.drawable.bg_bottom_nav_item_unselected
        )
        val textColor = context.getColor(if (isSelected) R.color.home_mint else R.color.text_secondary)
        tab.label.setTextColor(textColor)
        tab.icon.imageTintList = ColorStateList.valueOf(textColor)
        tab.container.alpha = if (isSelected) 1f else 0.9f
    }

    data class SectionTab(
        val container: LinearLayout,
        val label: TextView,
        val icon: ImageView
    )
}
