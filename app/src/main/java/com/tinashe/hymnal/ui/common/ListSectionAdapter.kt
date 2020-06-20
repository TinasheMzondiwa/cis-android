package com.tinashe.hymnal.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.ListSectionItemBinding
import com.tinashe.hymnal.extensions.view.inflateView

class ListSectionAdapter : RecyclerView.Adapter<SectionHolder>() {

    var title: String = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder =
        SectionHolder.create(parent)

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: SectionHolder, position: Int) {
        holder.binding.titleView.text = title
    }
}

class SectionHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

    val binding: ListSectionItemBinding by lazy { ListSectionItemBinding.bind(containerView) }

    companion object {
        fun create(parent: ViewGroup): SectionHolder = SectionHolder(
            inflateView(R.layout.list_section_item, parent, false)
        )
    }
}
