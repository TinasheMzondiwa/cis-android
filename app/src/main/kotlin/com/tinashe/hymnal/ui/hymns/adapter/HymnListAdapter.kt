package com.tinashe.hymnal.ui.hymns.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.databinding.HymnListItemBinding
import com.tinashe.hymnal.extensions.view.inflate

class HymnListAdapter(
    private val hymnSelected: (Pair<Hymn, View>) -> Unit
) : ListAdapter<Hymn, HymnListAdapter.TitleHolder>(HymnsDiff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TitleHolder = TitleHolder.create(parent).also { holder ->
        holder.itemView.setOnClickListener { view ->
            val hymn = getItem(holder.absoluteAdapterPosition)
            hymnSelected(hymn to view)
        }
    }

    override fun onBindViewHolder(holder: TitleHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class TitleHolder(
        private val binding: HymnListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hymn: Hymn) {
            binding.titleView.text = hymn.title
        }

        companion object {
            fun create(parent: ViewGroup): TitleHolder = TitleHolder(
                HymnListItemBinding.bind(parent.inflate(R.layout.hymn_list_item))
            )
        }
    }
}
