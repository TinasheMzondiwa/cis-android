package com.tinashe.hymnal.ui.collections.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.hymnal.data.model.collections.CollectionHymns

object CollectionsDiff : DiffUtil.ItemCallback<CollectionHymns>() {
    override fun areItemsTheSame(oldItem: CollectionHymns, newItem: CollectionHymns): Boolean {
        return oldItem.collection.collectionId == newItem.collection.collectionId
    }

    override fun areContentsTheSame(oldItem: CollectionHymns, newItem: CollectionHymns): Boolean {
        return oldItem == newItem
    }
}
