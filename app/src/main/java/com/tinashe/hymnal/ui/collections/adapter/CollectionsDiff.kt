package com.tinashe.hymnal.ui.collections.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.hymnal.data.model.HymnCollection

object CollectionsDiff : DiffUtil.ItemCallback<HymnCollection>() {
    override fun areItemsTheSame(oldItem: HymnCollection, newItem: HymnCollection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HymnCollection, newItem: HymnCollection): Boolean {
        return oldItem == newItem
    }
}
