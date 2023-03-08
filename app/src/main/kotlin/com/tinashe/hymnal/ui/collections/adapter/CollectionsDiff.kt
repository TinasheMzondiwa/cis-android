package com.tinashe.hymnal.ui.collections.adapter

import androidx.recyclerview.widget.DiffUtil
import hymnal.content.model.CollectionHymns

object CollectionsDiff : DiffUtil.ItemCallback<CollectionHymns>() {
    override fun areItemsTheSame(oldItem: CollectionHymns, newItem: CollectionHymns): Boolean {
        return oldItem.collection.collectionId == newItem.collection.collectionId
    }

    override fun areContentsTheSame(oldItem: CollectionHymns, newItem: CollectionHymns): Boolean {
        return oldItem == newItem
    }
}
