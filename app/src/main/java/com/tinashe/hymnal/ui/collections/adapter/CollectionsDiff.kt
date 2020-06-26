package com.tinashe.hymnal.ui.collections.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.hymnal.data.model.HymnCollectionModel

object CollectionsDiff : DiffUtil.ItemCallback<HymnCollectionModel>() {
    override fun areItemsTheSame(oldItem: HymnCollectionModel, newItem: HymnCollectionModel): Boolean {
        return oldItem.collection.id == newItem.collection.id
    }

    override fun areContentsTheSame(oldItem: HymnCollectionModel, newItem: HymnCollectionModel): Boolean {
        return oldItem == newItem
    }
}
