package com.example.moneymanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.Item
import com.example.moneymanager.ItemType
import com.example.moneymanager.R

class StaticPagesAdapter(private val pages: List<View>) :
    RecyclerView.Adapter<StaticPagesAdapter.PageHolder>() {

    class PageHolder(val container: ViewGroup) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return PageHolder(container)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val page = pages[position]
        (page.parent as? ViewGroup)?.removeView(page)
        holder.container.removeAllViews()
        holder.container.addView(page)
    }

    override fun getItemCount() = pages.size
}

class ItemsPagerAdapter(
    private val getItemsForTab: (Int) -> List<Item>,
    private val onAddEntry: () -> Unit,
    private val onItemLongClick: (Item) -> Unit,
    private val amountFormatter: (Item) -> String,
    private val itemMarkerFormatter: (ItemType) -> String
) : RecyclerView.Adapter<ItemsPagerAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_filtered_items, parent, false)
        return PageViewHolder(
            view = view,
            onAddEntry = onAddEntry,
            onItemLongClick = onItemLongClick,
            amountFormatter = amountFormatter,
            itemMarkerFormatter = itemMarkerFormatter
        )
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItemsForTab(position))
    }

    override fun getItemCount() = 4

    class PageViewHolder(
        view: View,
        private val onAddEntry: () -> Unit,
        private val onItemLongClick: (Item) -> Unit,
        private val amountFormatter: (Item) -> String,
        private val itemMarkerFormatter: (ItemType) -> String
    ) : RecyclerView.ViewHolder(view) {
        private val recyclerView: RecyclerView = view.findViewById(R.id.rvPageItems)
        private val emptyStateLayout: View = view.findViewById(R.id.layoutEmptyState)
        private val emptyState: TextView = view.findViewById(R.id.tvEmptyState)
        private val emptyAddButton: Button = view.findViewById(R.id.btnEmptyAddEntry)

        init {
            recyclerView.layoutManager = LinearLayoutManager(view.context)
            emptyAddButton.setOnClickListener { onAddEntry() }
        }

        fun bind(pageItems: List<Item>) {
            recyclerView.adapter = ItemAdapter(
                items = pageItems,
                onItemLongClick = onItemLongClick,
                amountFormatter = amountFormatter,
                itemMarkerFormatter = itemMarkerFormatter
            )
            val isEmpty = pageItems.isEmpty()
            emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
            emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
}
