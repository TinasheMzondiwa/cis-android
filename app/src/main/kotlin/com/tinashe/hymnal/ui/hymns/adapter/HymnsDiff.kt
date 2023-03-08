package com.tinashe.hymnal.ui.hymns.adapter

import androidx.recyclerview.widget.DiffUtil
import hymnal.content.model.Hymn

object HymnsDiff : DiffUtil.ItemCallback<Hymn>() {
    override fun areItemsTheSame(oldItem: Hymn, newItem: Hymn): Boolean {
        return oldItem.hymnId == newItem.hymnId
    }

    override fun areContentsTheSame(oldItem: Hymn, newItem: Hymn): Boolean {
        return oldItem == newItem
    }
}
