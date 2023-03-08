package com.tinashe.hymnal.ui.collections.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import hymnal.content.model.CollectionHymns

class CollectionListAdapter(
    private val callback: (Pair<CollectionHymns, View>) -> Unit
) :
    ListAdapter<CollectionHymns, CollectionHolder>(CollectionsDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        return CollectionHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener { callback(item to it) }
    }
}
