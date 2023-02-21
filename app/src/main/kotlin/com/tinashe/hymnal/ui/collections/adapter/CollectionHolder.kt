package com.tinashe.hymnal.ui.collections.adapter

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.databinding.HymnCollectionItemBinding
import com.tinashe.hymnal.extensions.view.inflateView
import hymnal.l10n.R as L10nR

class CollectionHolder(
    private val containerView: View
) : RecyclerView.ViewHolder(containerView) {

    private val binding: HymnCollectionItemBinding by lazy {
        HymnCollectionItemBinding.bind(containerView)
    }

    fun bind(model: CollectionHymns) {
        binding.apply {
            titleView.text = model.collection.title
            val resources = containerView.resources
            val count = model.hymns.size
            hymnsCount.text = when (count) {
                0 -> resources.getString(L10nR.string.hymn_count_zero)
                else -> resources.getQuantityString(L10nR.plurals.hymns_count, count, count)
            }
            descView.apply {
                text = model.collection.description
                isVisible = text.isNotEmpty()
            }
            dateView.text = buildSpannedString {
                append(containerView.resources.getString(L10nR.string.created_label))
                append(" ")
                bold { append(DateUtils.getRelativeTimeSpanString(model.collection.created)) }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): CollectionHolder = CollectionHolder(
            inflateView(R.layout.hymn_collection_item, parent, false)
        )
    }
}
