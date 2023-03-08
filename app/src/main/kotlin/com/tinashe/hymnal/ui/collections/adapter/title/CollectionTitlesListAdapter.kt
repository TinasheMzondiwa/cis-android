package com.tinashe.hymnal.ui.collections.adapter.title

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.HymnCollectionTitleItemBinding
import com.tinashe.hymnal.extensions.view.inflateView
import com.tinashe.hymnal.ui.collections.adapter.CollectionsDiff
import hymnal.content.model.CollectionHymns

class CollectionTitlesListAdapter(
    private val callback: (Pair<CollectionHymns, Boolean>) -> Unit
) :
    ListAdapter<CollectionHymns, CollectionTitlesListAdapter.TitleHolder>(CollectionsDiff) {

    var collectionSelectionMap: MutableMap<Int, Boolean> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleHolder {
        return TitleHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TitleHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, collectionSelectionMap[item.collection.collectionId] ?: false) {
            collectionSelectionMap[item.collection.collectionId] = it
            callback(item to it)
        }
        holder.itemView.setOnClickListener {
            val checked = !(collectionSelectionMap[item.collection.collectionId] ?: false)
            collectionSelectionMap[item.collection.collectionId] = checked
            callback(item to checked)
            notifyItemChanged(position)
        }
    }

    class TitleHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

        private val binding: HymnCollectionTitleItemBinding by lazy {
            HymnCollectionTitleItemBinding.bind(containerView)
        }

        fun bind(model: CollectionHymns, checked: Boolean, selectionChange: (Boolean) -> Unit) {
            binding.titleView.text = model.collection.title
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
