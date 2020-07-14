package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.hymnal.data.model.Hymnal

object HymnalsDiff : DiffUtil.ItemCallback<Hymnal>() {
    override fun areItemsTheSame(oldItem: Hymnal, newItem: Hymnal): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Hymnal, newItem: Hymnal): Boolean {
        return oldItem == newItem
    }
}
