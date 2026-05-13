package com.example.moneymanager.ui

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.Item
import com.example.moneymanager.ItemType
import com.example.moneymanager.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeItemsPagerController(
    private val context: Context,
    private val tabFilters: TabLayout,
    private val viewPagerItems: ViewPager2,
    private val getItemsForTab: (Int) -> List<Item>,
    private val onAddEntry: () -> Unit,
    private val onItemLongClick: (Item) -> Unit,
    private val amountFormatter: (Item) -> String,
    private val itemMarkerFormatter: (ItemType) -> String,
    private val tabTitleProvider: (Int) -> String
) {
    private lateinit var pagerAdapter: ItemsPagerAdapter
    private var tabMediator: TabLayoutMediator? = null

    fun setup() {
        pagerAdapter = ItemsPagerAdapter(
            getItemsForTab = getItemsForTab,
            onAddEntry = onAddEntry,
            onItemLongClick = onItemLongClick,
            amountFormatter = amountFormatter,
            itemMarkerFormatter = itemMarkerFormatter
        )
        viewPagerItems.adapter = pagerAdapter
        tabMediator?.detach()
        tabMediator = TabLayoutMediator(tabFilters, viewPagerItems) { tab, position ->
            tab.customView = createTabView(position)
        }
        tabMediator?.attach()
        tabFilters.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = updateTabViews()
            override fun onTabUnselected(tab: TabLayout.Tab) = updateTabViews()
            override fun onTabReselected(tab: TabLayout.Tab) = updateTabViews()
        })
        viewPagerItems.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabViews()
            }
        })
        updateTabViews()
    }

    fun refresh() {
        if (::pagerAdapter.isInitialized) {
            pagerAdapter.notifyDataSetChanged()
        }
        updateTabViews()
    }

    private fun createTabView(position: Int): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.item_filter_tab, tabFilters, false)
        val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
        val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
        tvTitle.text = tabTitleProvider(position)
        tvCount.text = getItemsForTab(position).size.toString()
        tvCount.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.tab_badge))
        updateTabViewStyle(tabView, position == viewPagerItems.currentItem)
        return tabView
    }

    private fun updateTabViews() {
        for (index in 0 until tabFilters.tabCount) {
            val tab = tabFilters.getTabAt(index) ?: continue
            val tabView = tab.customView ?: continue
            val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
            val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
            tvTitle.text = tabTitleProvider(index)
            tvCount.text = getItemsForTab(index).size.toString()
            tvCount.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.tab_badge))
            updateTabViewStyle(tabView, index == viewPagerItems.currentItem)
        }
    }

    private fun updateTabViewStyle(tabView: View, isSelected: Boolean) {
        val root = tabView as LinearLayout
        val tvTitle = tabView.findViewById<TextView>(R.id.tvTabTitle)
        val tvCount = tabView.findViewById<TextView>(R.id.tvTabCount)
        root.background = context.getDrawable(
            if (isSelected) R.drawable.bg_tab_chip_selected else R.drawable.bg_tab_chip_unselected
        )
        tvTitle.setTextColor(context.getColor(if (isSelected) R.color.home_dark_text else R.color.text_secondary))
        tvCount.alpha = if (isSelected) 1f else 0.82f
    }
}
