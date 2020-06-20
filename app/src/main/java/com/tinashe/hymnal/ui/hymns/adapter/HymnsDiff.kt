package com.tinashe.hymnal.ui.hymns.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.hymnal.data.model.Hymn

object HymnsDiff : DiffUtil.ItemCallback<Hymn>() {
    override fun areItemsTheSame(oldItem: Hymn, newItem: Hymn): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Hymn, newItem: Hymn): Boolean {
        return oldItem == newItem
    }
}
