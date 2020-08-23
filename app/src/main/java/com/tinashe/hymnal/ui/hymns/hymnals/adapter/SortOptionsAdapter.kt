package com.tinashe.hymnal.ui.hymns.hymnals.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.HymnalSort
import com.tinashe.hymnal.databinding.HymnsSortOrderBinding
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.extensions.view.inflateView

class SortOptionsAdapter(
    private val prefs: HymnalPrefs,
    private val sortChange: () -> Unit
) : RecyclerView.Adapter<SortHolder>() {

    private var setUp: Boolean = false

    fun init() {
        if (!setUp) {
            setUp = true
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SortHolder {
        return SortHolder.create(parent) {
            prefs.setHymnalSort(it)
            sortChange.invoke()
        }
    }

    override fun getItemCount(): Int = if (setUp) 1 else 0

    override fun onBindViewHolder(holder: SortHolder, position: Int) {
        holder.bind(prefs.getHymnalSort())
    }
}

class SortHolder(
    containerView: View,
    private val sortChange: (HymnalSort) -> Unit
) : RecyclerView.ViewHolder(containerView) {

    private val binding: HymnsSortOrderBinding by lazy {
        HymnsSortOrderBinding.bind(containerView)
    }

    private val changeListener: ChipGroup.OnCheckedChangeListener =
        ChipGroup.OnCheckedChangeListener { _, checkedId ->
            val labelRes: Int
            val sort: HymnalSort
            when (checkedId) {
                R.id.chipSortTitle -> {
                    sort = HymnalSort.TITLE
                    labelRes = R.string.sort_title
                }
                R.id.chipSortLanguage -> {
                    sort = HymnalSort.LANGUAGE
                    labelRes = R.string.sort_language
                }
                else -> return@OnCheckedChangeListener
            }
            val resources = containerView.resources
            binding.sortLabel.text = resources.getString(
                R.string.ordered_by,
                resources.getString(labelRes)
            )
            sortChange(sort)
        }

    init {
        containerView.setOnClickListener {
            binding.apply {
                sortGroup.isVisible = !sortGroup.isVisible
                sortHint.setText(if (sortGroup.isVisible) R.string.tap_to_close else R.string.tap_to_change)
                val rotation = when (sortChevIcon.rotation) {
                    0f -> 180f
                    else -> 0f
                }
                sortChevIcon.animate()
                    .rotation(rotation)
                    .start()
            }
        }
    }

    fun bind(sort: HymnalSort) {
        val labelRes = when (sort) {
            HymnalSort.TITLE -> R.string.sort_title
            HymnalSort.LANGUAGE -> R.string.sort_language
        }
        binding.sortLabel.apply {
            text = resources.getString(R.string.ordered_by, resources.getString(labelRes))
        }
        binding.sortGroup.apply {
            setOnCheckedChangeListener(null)
            check(
                when (sort) {
                    HymnalSort.TITLE -> R.id.chipSortTitle
                    HymnalSort.LANGUAGE -> R.id.chipSortLanguage
                }
            )
            setOnCheckedChangeListener(changeListener)
        }
    }

    companion object {
        fun create(parent: ViewGroup, sortChange: (HymnalSort) -> Unit): SortHolder =
            SortHolder(
                inflateView(R.layout.hymns_sort_order, parent, false),
                sortChange
            )
    }
}
