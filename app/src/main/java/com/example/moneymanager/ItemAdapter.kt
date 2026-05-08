package com.example.moneymanager

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ItemAdapter(
    private val items: List<Item>,
    private val onItemLongClick: (Item) -> Unit = {},
    private val amountFormatter: (Item) -> String = { defaultFormatSignedAmount(it.amount) },
    private val itemMarkerFormatter: (ItemType) -> String = { defaultItemMarker(it) }
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_simple, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        private val tvTrailing: TextView = itemView.findViewById(R.id.tvTrailing)

        fun bind(item: Item) {
            tvAvatar.text = itemMarkerFormatter(item.type)
            tvAvatar.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(itemView.context, getAvatarColor(item.type))
            )
            tvAvatar.setTextColor(ContextCompat.getColor(itemView.context, R.color.home_dark_text))
            tvName.text = item.name
            tvMeta.text = item.meta
            tvTrailing.text = item.trailing
            tvAmount.text = amountFormatter(item)

            val colorRes = when (item.type) {
                ItemType.INCOME -> R.color.amount_positive
                ItemType.TRANSFER -> R.color.balance_text
                else -> R.color.amount_negative
            }
            tvAmount.setTextColor(ContextCompat.getColor(itemView.context, colorRes))

            itemView.isClickable = true
            itemView.isFocusable = true
            itemView.setOnClickListener {
                onItemLongClick(item)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    companion object {
        private fun defaultItemMarker(type: ItemType): String {
            return when (type) {
                ItemType.INCOME -> "+"
                ItemType.SUBSCRIPTION -> "-"
                ItemType.RECURRING_EXPENSE -> "-"
                ItemType.EXPENSE -> "-"
                ItemType.TRANSFER -> "↔"
            }
        }

        private fun defaultFormatSignedAmount(value: Double): String {
            val absValue = kotlin.math.abs(value)
            return if (value >= 0) {
                String.format(Locale.getDefault(), "+%.2f", absValue)
            } else {
                String.format(Locale.getDefault(), "-%.2f", absValue)
            }
        }

        private fun getAvatarColor(type: ItemType): Int {
            return when (type) {
                ItemType.INCOME -> R.color.home_mint
                ItemType.SUBSCRIPTION -> R.color.home_yellow
                ItemType.RECURRING_EXPENSE -> R.color.home_yellow
                ItemType.EXPENSE -> R.color.home_pink
                ItemType.TRANSFER -> R.color.home_blue
            }
        }
    }
}
