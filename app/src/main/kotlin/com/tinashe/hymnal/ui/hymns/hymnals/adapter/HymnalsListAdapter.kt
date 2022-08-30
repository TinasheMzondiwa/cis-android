package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tinashe.hymnal.ui.hymns.hymnals.HymnalModel

class HymnalsListAdapter(private val hymnalSelected: (HymnalModel) -> Unit) :
    ListAdapter<HymnalModel, HymnalViewHolder>(HymnalModel.DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HymnalViewHolder =
        HymnalViewHolder.create(parent)

    override fun onBindViewHolder(holder: HymnalViewHolder, position: Int) {
        val hymnal = getItem(position)
        holder.bind(hymnal)

        holder.itemView.setOnClickListener {
            hymnalSelected(hymnal)
        }
    }
}
