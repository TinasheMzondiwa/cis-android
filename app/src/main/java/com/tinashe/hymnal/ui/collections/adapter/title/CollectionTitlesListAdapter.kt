package com.tinashe.hymnal.ui.collections.adapter.title

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.HymnCollection
import com.tinashe.hymnal.databinding.HymnCollectionTitleItemBinding
import com.tinashe.hymnal.extensions.view.inflateView
import com.tinashe.hymnal.ui.collections.adapter.CollectionsDiff

class CollectionTitlesListAdapter(
    private val callback: (Pair<HymnCollection, Boolean>) -> Unit
) :
    ListAdapter<HymnCollection, CollectionTitlesListAdapter.TitleHolder>(CollectionsDiff) {

    var collectionSelectionMap: MutableMap<Int, Boolean> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleHolder {
        return TitleHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TitleHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, collectionSelectionMap[item.id] ?: false) {
            collectionSelectionMap[item.id] = it
            callback(item to it)
        }
        holder.itemView.setOnClickListener {
            val checked = !(collectionSelectionMap[item.id] ?: false)
            collectionSelectionMap[item.id] = checked
            callback(item to checked)
            notifyItemChanged(position)
        }
    }

    class TitleHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

        private val binding: HymnCollectionTitleItemBinding by lazy {
            HymnCollectionTitleItemBinding.bind(containerView)
        }

        fun bind(item: HymnCollection, checked: Boolean, selectionChange: (Boolean) -> Unit) {
            binding.titleView.text = item.title
            binding.checkBox.apply {
                setOnCheckedChangeListener(null)
                isChecked = checked
                setOnCheckedChangeListener { _, checked ->
                    selectionChange(checked)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): TitleHolder = TitleHolder(
                inflateView(R.layout.hymn_collection_title_item, parent, false)
            )
        }
    }
}
