package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.data.model.Hymnal

class HymnalsListAdapter(private val hymnalSelected: (Hymnal) -> Unit) :
    RecyclerView.Adapter<HymnalViewHolder>() {

    var hymnals: List<Hymnal> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HymnalViewHolder =
        HymnalViewHolder.create(parent)

    override fun getItemCount(): Int = hymnals.size

    override fun onBindViewHolder(holder: HymnalViewHolder, position: Int) {
        val hymnal = hymnals[position]
        holder.bind(hymnal)

        holder.itemView.setOnClickListener {
            hymnalSelected(hymnal)
        }
    }
}
