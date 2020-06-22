package com.tinashe.hymnal.ui.hymns.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.databinding.HymnListItemBinding
import com.tinashe.hymnal.extensions.view.inflateView

class HymnListAdapter(
    private val hymnSelected: (Hymn) -> Unit
) : ListAdapter<Hymn, HymnListAdapter.TitleHolder>(HymnsDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleHolder =
        TitleHolder.create(parent)

    override fun onBindViewHolder(holder: TitleHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            hymnSelected(item)
        }
    }

    class TitleHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

        private val binding: HymnListItemBinding by lazy { HymnListItemBinding.bind(containerView) }

        fun bind(hymn: Hymn) {
            binding.titleView.text = hymn.title
        }

        companion object {
            fun create(parent: ViewGroup): TitleHolder = TitleHolder(
                inflateView(R.layout.hymn_list_item, parent, false)
            )
        }
    }
}
