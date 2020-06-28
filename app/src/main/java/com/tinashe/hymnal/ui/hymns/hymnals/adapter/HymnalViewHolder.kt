package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.remote.RemoteHymnal
import com.tinashe.hymnal.databinding.HymnalListItemBinding
import com.tinashe.hymnal.extensions.tint
import com.tinashe.hymnal.extensions.toColor
import com.tinashe.hymnal.extensions.view.inflateView

class HymnalViewHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

    private val binding: HymnalListItemBinding by lazy { HymnalListItemBinding.bind(containerView) }

    fun bind(hymnal: RemoteHymnal) {
        binding.apply {
            icon.background.tint(COLORS[absoluteAdapterPosition % COLORS.size].toColor())
            hymnalTitle.text = hymnal.title
            hymnalLanguage.text = hymnal.language
        }
    }

    companion object {
        /**
         * Adventist Identity colors
         *
         * https://identity.adventist.org/global-elements/color/
         */
        private val COLORS = arrayListOf("#4b207f", "#5e3929", "#7f264a", "#2f557f", "#e36520", "#448d21", "#3e8391")

        fun create(parent: ViewGroup): HymnalViewHolder = HymnalViewHolder(
            inflateView(R.layout.hymnal_list_item, parent, false)
        )
    }
}
